package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.AddMovieRequest;
import com.mgrunt.movies.domain.dtos.UserWatchListResponse;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface WatchlistService {

    UserWatchListResponse getWatchlist(Authentication authentication);

    void removeFromToWatch(Authentication authentication, UUID movieId);

    void removeFromWatched(Authentication authentication, UUID movieId);

    void markAsToWatch(UUID movieId, Authentication authentication);

    void markAsWatched(UUID movieId, Authentication authentication);

    void toggleMovie(UUID movieId, String listType, Authentication authentication);
}
