package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.tmdb.*;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.exceptions.ExternalApiException;
import com.mgrunt.movies.services.TmdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TmdbServiceImpl implements TmdbService {

    private static final int DEFAULT_BACKDROPS_LIMIT = 10;

    private final RestTemplate restTemplate;

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    @Value("${tmdb.base.url:https://api.themoviedb.org/3}")
    private String tmdbBaseUrl;

    private <T> T fetchFromTmdb(String url, Class<T> responseType) {
        HttpEntity<String> entity = new HttpEntity<>(buildTmdbHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, entity, responseType).getBody();
    }

    private HttpHeaders buildTmdbHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        headers.set("Authorization", "Bearer " + tmdbApiKey);
        return headers;
    }

    @Override
    public TmdbMovieDetailsResponse fetchRawMovieDetails(Long movieId) {
        try {
            String url = tmdbBaseUrl + "/movie/" + movieId + "?language=en-US";
            return fetchFromTmdb(url, TmdbMovieDetailsResponse.class);
        } catch (Exception e) {
            log.error("Error fetching movie details for ID: {}", movieId, e);
            throw new ExternalApiException("Failed to fetch movie details from TMDB", e);
        }
    }

    @Override
    public List<TmdbVideoResponse> getMovieVideos(Long movieId) {
        try {
            String url = tmdbBaseUrl + "/movie/" + movieId + "/videos?language=en-US";
            TmdbVideosWrapperResponse response = fetchFromTmdb(url, TmdbVideosWrapperResponse.class);
            return response != null ? response.getResults() : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching movie videos for ID: {}", movieId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public String getTrailerUrl(Long movieId) {
        try {
            List<TmdbVideoResponse> videos = getMovieVideos(movieId);

            Optional<TmdbVideoResponse> trailer = videos.stream()
                    .filter(v -> "Trailer".equals(v.getType())
                            && "YouTube".equals(v.getSite())
                            && Boolean.TRUE.equals(v.getOfficial()))
                    .findFirst();

            if (trailer.isEmpty()) {
                trailer = videos.stream()
                        .filter(v -> "Trailer".equals(v.getType()) && "YouTube".equals(v.getSite()))
                        .findFirst();
            }

            return trailer.map(v -> "https://www.youtube.com/watch?v=" + v.getKey()).orElse(null);
        } catch (Exception e) {
            log.error("Error getting trailer URL for movie ID: {}", movieId, e);
            return null;
        }
    }

    public List<String> getImages(Long movieId, int limit){
        String url = tmdbBaseUrl + "/movie/" + movieId + "/images";
        TmdbImageWrapperResponse response = fetchFromTmdb(url, TmdbImageWrapperResponse.class);

        return Optional.ofNullable(response)
                .map(TmdbImageWrapperResponse::getBackdrops)
                .orElseGet(Collections::emptyList)
                .stream()
                .limit(limit)
                .map(TmdbImageResponse::getFilePath)
                .toList();
    }

    public List<TmdbTrendingMovieResponse> getTrendingMovies() {
        try {
            String url = tmdbBaseUrl + "/trending/movie/day";
            TmdbTrendingMoviesResponseWrapper response = fetchFromTmdb(url, TmdbTrendingMoviesResponseWrapper.class);
            return Optional.ofNullable(response)
                    .map(TmdbTrendingMoviesResponseWrapper::getResults)
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .limit(10)
                    .toList();
        } catch (Exception e) {
            log.error("Error fetching trending movies", e);
            return Collections.emptyList();
        }
    }

    @Override
    public TmdbSearchResponse searchResult(String query, int page) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = tmdbBaseUrl + "/search/movie?query=" + encodedQuery + "&language=en-US&page=" + page;
            return fetchFromTmdb(url, TmdbSearchResponse.class);
        } catch (Exception e) {
            log.error("Error getting search results with query: {}", query, e);
            return null;
        }
    }

    @Override
    public void syncMovieData(Movie movie, Long tmdbId) {
        TmdbMovieDetailsResponse tmdbMovie = fetchRawMovieDetails(tmdbId);

        movie.setTmdbId(tmdbId);
        movie.setImdbId(tmdbMovie.getImdbId());
        movie.setTitle(tmdbMovie.getTitle());
        movie.setOverview(tmdbMovie.getOverview());
        movie.setReleaseDate(tmdbMovie.getReleaseDate());
        movie.setPosterPath(tmdbMovie.getPosterPath());
        movie.setBackdropPath(tmdbMovie.getBackdropPath());
        movie.setVoteAverage(tmdbMovie.getVoteAverage());
        movie.setVoteCount(tmdbMovie.getVoteCount());
        movie.setPopularity(tmdbMovie.getPopularity());
        movie.setRuntime(tmdbMovie.getRuntime());
        movie.setOriginalLanguage(tmdbMovie.getOriginalLanguage());
        movie.setAdult(tmdbMovie.getAdult());
        movie.setBudget(tmdbMovie.getBudget());
        movie.setRevenue(tmdbMovie.getRevenue());
        movie.setTagline(tmdbMovie.getTagline());
        movie.setTrailerUrl(getTrailerUrl(tmdbId));

        movie.getBackdrops().clear();
        movie.getBackdrops().addAll(getImages(tmdbId, DEFAULT_BACKDROPS_LIMIT));

        List<String> genreNames = Optional.ofNullable(tmdbMovie.getGenres())
                .orElse(Collections.emptyList())
                .stream()
                .map(TmdbGenreResponse::getName)
                .distinct()
                .toList();

        movie.getGenres().clear();
        movie.getGenres().addAll(genreNames);
    }
}