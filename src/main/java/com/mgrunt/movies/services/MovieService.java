package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.MovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.MovieDto;
import com.mgrunt.movies.domain.dtos.MovieSearchResponse;
import com.mgrunt.movies.domain.dtos.TmdbSearchResponse;
import com.mgrunt.movies.domain.entities.Movie;

import java.util.List;
import java.util.UUID;

public interface MovieService {
    List<MovieDto> getAllMovies();
    MovieDto getSingleMovie(String imdbId);

    List<MovieDetailsResponse> getRandomMovies();

    MovieDetailsResponse getMovieDetails(Long movieId);

    List<MovieSearchResponse> searchMovies(String query, int limit);

    Movie findOrCreateMovie(Long tmdbId);

    TmdbSearchResponse getSearchResults(String query, int page);
//    void addMovieToWatchlist(UUID userId, String imdbId);
}
