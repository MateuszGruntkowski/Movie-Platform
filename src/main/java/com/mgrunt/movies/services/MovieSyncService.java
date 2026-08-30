package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.entities.Movie;

public interface MovieSyncService {

    Movie getOrSyncMovie(Long tmdbId);
    Movie getMovieForView(Long tmdbId);
}
