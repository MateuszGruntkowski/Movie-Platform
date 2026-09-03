package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.movie.MovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.movie.MovieSearchResponse;
import com.mgrunt.movies.domain.dtos.movie.TrendingMovieResponse;
import com.mgrunt.movies.domain.entities.Movie;

import java.util.List;

public interface MovieService {
    Movie getOrCreatePersistedMovie(Long tmdbId);
    List<TrendingMovieResponse> getTrendingMovies();
    MovieDetailsResponse getMovieDetails(Long tmdbId);
    MovieSearchResponse searchMovies(String query, int page);
}