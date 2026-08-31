package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.watchlist.UserWatchListResponse;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface WatchlistService {

    UserWatchListResponse getWatchlistByUserId(UUID userID);

    void toggleMovie(Long tmdbId, String listType, UUID userId);
}
