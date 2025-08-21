package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.MovieDto;

import java.util.List;
import java.util.UUID;

public interface MovieService {
    List<MovieDto> getAllMovies();
    MovieDto getSingleMovie(String imdbId);

    List<MovieDto> getRandomMovies();
//    void addMovieToWatchlist(UUID userId, String imdbId);
}
