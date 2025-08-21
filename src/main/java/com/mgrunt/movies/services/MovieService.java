package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.MovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.MovieDto;
import com.mgrunt.movies.domain.dtos.MovieSearchResponse;

import java.util.List;
import java.util.UUID;

public interface MovieService {
    List<MovieDto> getAllMovies();
    MovieDto getSingleMovie(String imdbId);

    List<MovieDto> getRandomMovies();

    MovieDetailsResponse getMovieDetails(Long movieId);

    List<MovieSearchResponse> searchMovies(String query, int limit);
//    void addMovieToWatchlist(UUID userId, String imdbId);
}
