package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.movie.MovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.movie.MovieSearchResponse;
import com.mgrunt.movies.domain.dtos.movie.TrendingMovieResponse;
import com.mgrunt.movies.domain.entities.Movie;

import java.util.List;

public interface MovieService {
    List<TrendingMovieResponse> getTrendingMovies();
    MovieDetailsResponse getMovieDetails(Long tmdbId);
    MovieSearchResponse searchMovies(String query, int page);
    /** Returns the local Movie entity for the given tmdbId - retrieves from the TMDB and saves if it is missing or the data is stale (> 24h). */
    Movie getMovie(Long tmdbId);
}