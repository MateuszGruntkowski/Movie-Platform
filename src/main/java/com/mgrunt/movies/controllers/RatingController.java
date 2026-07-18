package com.mgrunt.movies.controllers;

import com.mgrunt.movies.Security.CustomUserDetails;
import com.mgrunt.movies.domain.dtos.rating.RatingRequest;
import com.mgrunt.movies.domain.dtos.rating.RatingDto;
import com.mgrunt.movies.services.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RatingController {

    private final RatingService ratingService;

    @GetMapping("/movies/{tmdbId}/ratings/me")
    public ResponseEntity<RatingDto> getMyRating(
            @PathVariable Long tmdbId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Optional<RatingDto> ratingResponse = ratingService.getUserRatingForMovie(tmdbId, userDetails.getId());
        return ratingResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping("movies/{tmdbID}/ratings/me")
    public ResponseEntity<RatingDto> updateRating(
            @PathVariable Long tmdbID,
            @Valid @RequestBody RatingRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        RatingDto response = ratingService.rateMovie(tmdbID, userDetails.getId(), request.rating());
        return ResponseEntity.ok(response);
    }
}
