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
import org.springframework.http.ResponseEntity;
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

    @Value("${tmdb.image.base.url:https://image.tmdb.org/t/p}")
    private String tmdbImageBaseUrl;

    @Override
    public TmdbMovieDetailsResponse getMovieDetails(Long movieId) {
        try {
            String url = tmdbBaseUrl + "/movie/" + movieId + "?language=en-US";

            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "application/json");
            headers.set("Authorization", "Bearer " + tmdbApiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<TmdbMovieDetailsResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    TmdbMovieDetailsResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Error fetching movie details for ID: {}", movieId, e);
            throw new ExternalApiException("Failed to fetch movie details from TMDB", e);
        }
    }

    @Override
    public String getImageUrl(String imagePath, String size) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return null;
        }
        return tmdbImageBaseUrl + "/" + (size != null ? size : "w500") + imagePath;
    }

    @Override
    public List<TmdbVideoResponse> getMovieVideos(Long movieId) {
        try {
            String url = tmdbBaseUrl + "/movie/" + movieId + "/videos?language=en-US";

            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "application/json");
            headers.set("Authorization", "Bearer " + tmdbApiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<TmdbVideosWrapperResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    TmdbVideosWrapperResponse.class
            );

            return response.getBody() != null ? response.getBody().getResults() : Collections.emptyList();

        } catch (Exception e) {
            log.error("Error fetching movie videos for ID: {}", movieId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public String getTrailerUrl(Long movieId) {
        try {
            List<TmdbVideoResponse> videos = getMovieVideos(movieId);

            // Szukaj oficjalnego trailera na YouTube
            Optional<TmdbVideoResponse> trailer = videos.stream()
                    .filter(video -> "Trailer".equals(video.getType())
                            && "YouTube".equals(video.getSite())
                            && Boolean.TRUE.equals(video.getOfficial()))
                    .findFirst();

            // Jeśli nie ma oficjalnego, szukaj dowolnego trailera na YouTube
            if (trailer.isEmpty()) {
                trailer = videos.stream()
                        .filter(video -> "Trailer".equals(video.getType())
                                && "YouTube".equals(video.getSite()))
                        .findFirst();
            }

            return trailer
                    .map(video -> "https://www.youtube.com/watch?v=" + video.getKey())
                    .orElse(null);

        } catch (Exception e) {
            log.error("Error getting trailer URL for movie ID: {}", movieId, e);
            return null;
        }
    }

    @Override
    public List<String> getMovieBackdrops(Long movieId, int limit) {
        try {
            List<String> backdrops = new ArrayList<>();

            // Pobierz szczegóły filmu
            TmdbMovieDetailsResponse movieDetails = getMovieDetails(movieId);

            // Dodaj główny backdrop filmu
            if (movieDetails.getBackdropPath() != null) {
                backdrops.add(getImageUrl(movieDetails.getBackdropPath(), "original"));
            }

            // Sprawdź czy film należy do kolekcji
            if (movieDetails.getBelongsToCollection() != null
                    && movieDetails.getBelongsToCollection().getId() != null) {

                List<String> collectionBackdrops = getCollectionBackdrops(
                        movieDetails.getBelongsToCollection().getId()
                );

                // Dodaj backdrops z kolekcji (filtruj duplikaty)
                collectionBackdrops.stream()
                        .map(backdrop -> getImageUrl(backdrop, "original"))
                        .filter(backdrop -> !backdrops.contains(backdrop))
                        .forEach(backdrops::add);
            }

            return backdrops.stream()
                    .limit(limit)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching movie backdrops for ID: {}", movieId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getCollectionBackdrops(Long collectionId) {
        try {
            String url = tmdbBaseUrl + "/collection/" + collectionId + "/images";

            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "application/json");
            headers.set("Authorization", "Bearer " + tmdbApiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<TmdbCollectionImagesResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    TmdbCollectionImagesResponse.class
            );

            return response.getBody() != null && response.getBody().getBackdrops() != null
                    ? response.getBody().getBackdrops().stream()
                    .map(TmdbImageResponse::getFilePath)
                    .collect(Collectors.toList())
                    : Collections.emptyList();

        } catch (Exception e) {
            log.error("Error fetching collection backdrops for ID: {}", collectionId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<TmdbMovieSearchResult> searchMovies(String query, int limit) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = tmdbBaseUrl + "/search/movie?query=" + encodedQuery + "&language=en-US&page=1";

            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "application/json");
            headers.set("Authorization", "Bearer " + tmdbApiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<TmdbSearchResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    TmdbSearchResponse.class
            );

            return response.getBody() != null && response.getBody().getResults() != null
                    ? response.getBody().getResults().stream()
                    .limit(limit)
                    .collect(Collectors.toList())
                    : Collections.emptyList();

        } catch (Exception e) {
            log.error("Error searching movies with query: {}", query, e);
            return Collections.emptyList();
        }
    }

    @Override
    public TmdbSearchResponse searchResult(String query, int page){
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = tmdbBaseUrl + "/search/movie?query=" + encodedQuery + "&language=en-US&page=" + page;

            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "application/json");
            headers.set("Authorization", "Bearer " + tmdbApiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<TmdbSearchResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    TmdbSearchResponse.class
            );
            return response.getBody() != null && response.getBody().getResults() != null
                    ? response.getBody()
                    : null;
        }catch (Exception e){
            log.error("Error getting search results with query: {}", query, e);
            return null;
        }
    }

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

        // Pobierz trailer URL
        String trailerUrl = getTrailerUrl(movieId);
        movie.setTrailerUrl(trailerUrl);

        // Pobierz backdrops (ograniczone do 10)
        List<String> backdrops = getMovieBackdrops(movieId, 10);
        movie.setBackdrops(backdrops);

        for (TmdbGenreResponse g : tmdbMovie.getGenres()){
            if(!movie.getGenres().contains(g.getName())) {
                movie.getGenres().add(g.getName());
            }
        }
        return movie;
    }

}
