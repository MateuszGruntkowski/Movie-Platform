package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.MovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.MovieSearchResponse;
import com.mgrunt.movies.domain.dtos.TmdbSearchResponse;
import com.mgrunt.movies.domain.entities.Movie;

import java.util.List;

public interface MovieService {

    List<MovieDetailsResponse> getRandomMovies();

    MovieDetailsResponse getMovieDetails(Long movieId);

    List<MovieSearchResponse> searchMovies(String query, int limit);

    Movie findOrCreateMovie(Long tmdbId);

    TmdbSearchResponse getSearchResults(String query, int page);

}
