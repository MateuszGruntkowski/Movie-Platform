package com.mgrunt.movies.services;
import com.mgrunt.movies.domain.dtos.UserWatchListResponse;
import org.springframework.security.core.Authentication;

public interface WatchlistService {

    UserWatchListResponse getWatchlist(Authentication authentication);

    void toggleMovie(Long tmdbId, String listType, Authentication authentication);
}
