package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.movie.MovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.movie.MovieSearchPageResponse;
import com.mgrunt.movies.domain.entities.Movie;

import java.util.List;

public interface MovieService {
    List<MovieDetailsResponse> getRandomMovies();
    MovieDetailsResponse getMovieDetails(Long tmdbId);
    MovieSearchPageResponse searchMovies(String query, int page);
    /** Returns the local Movie entity for the given tmdbId - retrieves from the TMDB and saves if it is missing or the data is stale (> 24h). */
    Movie getMovie(Long tmdbId);
}