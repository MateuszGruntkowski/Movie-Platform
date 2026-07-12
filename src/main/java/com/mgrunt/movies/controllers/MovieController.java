package com.mgrunt.movies.controllers;

import com.mgrunt.movies.domain.dtos.MovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.MovieSearchPageResponse;
import com.mgrunt.movies.services.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<List<MovieDetailsResponse>> listRandomMovies() {
        return new ResponseEntity<>(movieService.getRandomMovies(), HttpStatus.OK);
    }

    @GetMapping("/{movieId}/details")
    public ResponseEntity<MovieDetailsResponse> getMovieDetails(@PathVariable Long movieId) {
        return ResponseEntity.ok(movieService.getMovieDetails(movieId));
    }

    @GetMapping("/search")
    public ResponseEntity<MovieSearchPageResponse> searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(movieService.searchMovies(query, page));
    }
}