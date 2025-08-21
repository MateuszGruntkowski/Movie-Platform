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

    @PatchMapping(path = "/toWatch/{movieId}")
    public ResponseEntity<Void> markAsToWatch(
            @PathVariable UUID movieId,
            Authentication authentication){
        watchlistService.markAsToWatch(
                movieId,
                authentication
        );
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "/watched/{movieId}")
    public ResponseEntity<Void> markAsWatched(
            @PathVariable UUID movieId,
            Authentication authentication) {
        watchlistService.markAsWatched(
                movieId,
                authentication
        );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path="toWatch/{movieId}")
    public ResponseEntity<Void> removeFromToWatch(
            @PathVariable UUID movieId,
            Authentication authentication) {
        watchlistService.removeFromToWatch(authentication, movieId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path="watched/{movieId}")
    public ResponseEntity<Void> removeFromWatched(
            @PathVariable UUID movieId,
            Authentication authentication) {
        watchlistService.removeFromWatched(authentication, movieId);
        return ResponseEntity.ok().build();
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
