package com.mgrunt.movies.controllers;

import com.mgrunt.movies.domain.dtos.AddMovieRequest;
import com.mgrunt.movies.domain.dtos.UserWatchListResponse;
import com.mgrunt.movies.services.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @GetMapping
    public ResponseEntity<UserWatchListResponse> getWatchlist(Authentication authentication) {
        return new ResponseEntity<>(
                watchlistService.getWatchlist(authentication),
                HttpStatus.OK
        );
    }

    @PutMapping("/toggle/{tmdbId}")
    public ResponseEntity<Void> toggleMovie(
            @PathVariable Long tmdbId,
            @RequestParam String listType,
            Authentication authentication
    ) {
        watchlistService.toggleMovie(tmdbId, listType, authentication);
        return ResponseEntity.ok().build();
    }
}
