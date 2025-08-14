package com.mgrunt.movies.controllers;

import com.mgrunt.movies.domain.documents.Movie;
import com.mgrunt.movies.domain.dtos.MovieDto;
import com.mgrunt.movies.services.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<List<Movie>> ListAllMovies(){
        return new ResponseEntity<>(movieService.getAllMovies(), HttpStatus.OK);
    }

    @GetMapping(path = "/{imdbId}")
    public ResponseEntity<MovieDto> getMovie(@PathVariable String imdbId) {
        return new ResponseEntity<>(movieService.getSingleMovie(imdbId),HttpStatus.OK);
    }
}
