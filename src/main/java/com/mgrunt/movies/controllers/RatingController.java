package com.mgrunt.movies.controllers;

import com.mgrunt.movies.domain.dtos.rating.RatingResponse;
import com.mgrunt.movies.services.RatingService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ratings")
public class RatingController {

    private final RatingService ratingService;

    public ResponseEntity<RatingResponse> getMyRating(
            @PathVariable Long tmdbId,
            Authentication auth
    ) {
        Optional<RatingResponse> ratingResponse = ratingService.getUserRatingForMovie(tmdbId, auth);
        if (ratingResponse.isPresent()) {
            return ResponseEntity.ok(ratingResponse.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
