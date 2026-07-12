package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.*;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TmdbServiceImpl implements TmdbService {

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
    public TmdbMovieDetailsResponse getMovieDetails(Long movieId) {
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

    @Override
    public List<String> getMovieBackdrops(Long movieId, int limit) {
        try {
            List<String> backdrops = new ArrayList<>();
            TmdbMovieDetailsResponse movieDetails = getMovieDetails(movieId);

            if (movieDetails.getBackdropPath() != null) {
                backdrops.add(movieDetails.getBackdropPath());
            }

            if (movieDetails.getBelongsToCollection() != null
                    && movieDetails.getBelongsToCollection().getId() != null) {

                getCollectionBackdrops(movieDetails.getBelongsToCollection().getId()).stream()
                        .filter(backdrop -> !backdrops.contains(backdrop))
                        .forEach(backdrops::add);
            }

            return backdrops.stream().limit(limit).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching movie backdrops for ID: {}", movieId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getCollectionBackdrops(Long collectionId) {
        try {
            String url = tmdbBaseUrl + "/collection/" + collectionId + "/images";
            TmdbCollectionImagesResponse response = fetchFromTmdb(url, TmdbCollectionImagesResponse.class);

            return response != null && response.getBackdrops() != null
                    ? response.getBackdrops().stream()
                    .map(TmdbImageResponse::getFilePath)
                    .collect(Collectors.toList())
                    : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching collection backdrops for ID: {}", collectionId, e);
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
    public Movie createMovieFromTmdbData(Long movieId) {
        TmdbMovieDetailsResponse tmdbMovie = getMovieDetails(movieId);

        Movie movie = new Movie();
        movie.setTmdbId(tmdbMovie.getId());
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
        movie.setTrailerUrl(getTrailerUrl(movieId));
        movie.setBackdrops(getMovieBackdrops(movieId, 10));

        for (TmdbGenreResponse g : tmdbMovie.getGenres()) {
            if (!movie.getGenres().contains(g.getName())) {
                movie.getGenres().add(g.getName());
            }
        }
        return movie;
    }
}