package com.mgrunt.movies.controllers;

import com.mgrunt.movies.Security.CustomUserDetails;
import com.mgrunt.movies.domain.dtos.watchlist.UserWatchListResponse;
import com.mgrunt.movies.services.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @GetMapping
    public ResponseEntity<UserWatchListResponse> getWatchlist(
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        return new ResponseEntity<>(
                watchlistService.getWatchlistByUserId(userDetails.getId()),
                HttpStatus.OK
        );
    }

    @PutMapping("/toggle/{tmdbId}")
    public ResponseEntity<Void> toggleMovie(
            @PathVariable Long tmdbId,
            @RequestParam String listType,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        watchlistService.toggleMovie(tmdbId, listType, userDetails.getId());
        return ResponseEntity.ok().build();
    }
}
