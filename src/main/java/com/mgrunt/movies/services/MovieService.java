package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.movie.MovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.movie.MovieSearchPageResponse;
import com.mgrunt.movies.domain.entities.Movie;

import java.util.List;

public interface MovieService {
    List<MovieDetailsResponse> getRandomMovies();
    MovieDetailsResponse getMovieDetails(Long movieId);
    MovieSearchPageResponse searchMovies(String query, int page);
    Movie findOrCreateMovie(Long tmdbId);
}