package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.movie.WatchlistMovieResponse;
import com.mgrunt.movies.domain.dtos.watchlist.UserWatchListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface WatchlistService {

//    UserWatchListResponse getWatchlistByUserId(UUID userID);
    Page<WatchlistMovieResponse> getMoviesToWatch(UUID userId, Pageable pageable);
    Page<WatchlistMovieResponse> getMoviesWatched(UUID userId, Pageable pageable);
    void toggleMovie(Long tmdbId, String listType, UUID userId);
}
