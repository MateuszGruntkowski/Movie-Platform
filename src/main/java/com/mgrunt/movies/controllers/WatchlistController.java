package com.mgrunt.movies.controllers;

import com.mgrunt.movies.Security.CustomUserDetails;
import com.mgrunt.movies.domain.dtos.movie.WatchlistMovieResponse;
import com.mgrunt.movies.domain.dtos.watchlist.WatchlistStatusDto;
import com.mgrunt.movies.services.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @GetMapping("/to-watch")
    public ResponseEntity<Page<WatchlistMovieResponse>> getMoviesToWatch(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "title") Pageable pageable
    ) {
        return ResponseEntity.ok(
                watchlistService.getMoviesToWatch(userDetails.getId(), pageable)
        );
    }

    @GetMapping("/watched")
    public ResponseEntity<Page<WatchlistMovieResponse>> getMoviesWatched(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "title") Pageable pageable
    ) {
        return ResponseEntity.ok(
                watchlistService.getMoviesWatched(userDetails.getId(), pageable)
        );
    }

    @PutMapping("/toggle/{tmdbId}")
    public ResponseEntity<WatchlistStatusDto> toggleMovie(
            @PathVariable Long tmdbId,
            @RequestParam String listType,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        return ResponseEntity.ok(
                watchlistService.toggleMovie(tmdbId, listType, userDetails.getId())
        );
    }
}
