package com.mgrunt.movies.controllers;

import com.mgrunt.movies.domain.dtos.*;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.domain.entities.Review;
import com.mgrunt.movies.exceptions.MovieDetailsException;
import com.mgrunt.movies.exceptions.MovieSearchException;
import com.mgrunt.movies.services.Impl.TmdbServiceImpl;
import com.mgrunt.movies.services.MovieService;
import com.mgrunt.movies.services.ReviewService;
import com.mgrunt.movies.services.TmdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
@Slf4j
public class MovieController {

    private final MovieService movieService;
    private final TmdbService tmdbService;
    private final ReviewService reviewService;

    // NIEUŻYWANE
//    @GetMapping(path = "/all")
//    public ResponseEntity<List<MovieDto>> ListAllMovies(){
//        return new ResponseEntity<>(movieService.getAllMovies(), HttpStatus.OK);
//    }

    // NIEUZTWANE
//    @GetMapping(path = "/{imdbId}")
//    public ResponseEntity<MovieDto> getMovie(@PathVariable String imdbId) {
//        return new ResponseEntity<>(movieService.getSingleMovie(imdbId),HttpStatus.OK);
//    }

    @GetMapping
    public ResponseEntity<List<MovieDto>> ListRandomMovies(){
        return new ResponseEntity<>(movieService.getRandomMovies(), HttpStatus.OK);
    }

    // TMDB METHODS

    @GetMapping("/{movieId}/details")
    public ResponseEntity<MovieDetailsResponse> getMovieDetails(@PathVariable Long movieId) {
        try {
            MovieDetailsResponse response = movieService.getMovieDetails(movieId);
            return ResponseEntity.ok(response);
        } catch (MovieDetailsException e) {
            log.error("Error in controller getting movie details for ID: {}", movieId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{movieId}/videos")
    public ResponseEntity<List<TmdbVideoResponse>> getMovieVideos(@PathVariable Long movieId) {
        try {
            List<TmdbVideoResponse> videos = tmdbService.getMovieVideos(movieId);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            log.error("Error fetching movie videos for ID: {}", movieId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{movieId}/trailer")
    public ResponseEntity<TrailerResponse> getMovieTrailer(@PathVariable Long movieId) {
        try {
            String trailerUrl = tmdbService.getTrailerUrl(movieId);
            TrailerResponse response = new TrailerResponse(trailerUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching movie trailer for ID: {}", movieId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{movieId}/backdrops")
    public ResponseEntity<BackdropsResponse> getMovieBackdrops(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<String> backdrops = tmdbService.getMovieBackdrops(movieId, limit);
            BackdropsResponse response = new BackdropsResponse(backdrops);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching movie backdrops for ID: {}", movieId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieSearchResponse>> searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "8") int limit) {
        try {
            List<MovieSearchResponse> response = movieService.searchMovies(query, limit);
            return ResponseEntity.ok(response);
        } catch (MovieSearchException e) {
            log.error("Error in controller searching movies with query: {}", query, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
