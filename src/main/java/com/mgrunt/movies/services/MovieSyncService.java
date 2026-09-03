package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.entities.Movie;

public interface MovieSyncService {

    /**
     * Returns a movie GUARANTEED to be persisted in the database (managed entity).
     * If it didn't exist or the data is stale — fetches from TMDB and saves it.
     * Used wherever the movie needs to be associated with another entity (Rating, Review, WatchlistEntry).
     */
    Movie getOrCreatePersistedMovie(Long tmdbId);

    /**
     * Returns movie data for display (movie details page).
     * If the movie does NOT exist in the database — returns the data "on the fly", saves NOTHING.
     * If the movie ALREADY exists and is stale — refreshes and updates the existing record
     * (this is intentional cache behavior, not creation of new data).
     */
    Movie getMovieForDisplay(Long tmdbId);
}
