package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.AddMovieRequest;
import com.mgrunt.movies.domain.dtos.UserWatchListResponse;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface WatchlistService {

    UserWatchListResponse getWatchlist(Authentication authentication);

    void toggleMovie(Long tmdbId, String listType, Authentication authentication);
}
