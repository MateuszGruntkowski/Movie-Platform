package com.mgrunt.movies.controllers;

import com.mgrunt.movies.domain.dtos.ReviewDto;
import com.mgrunt.movies.domain.dtos.ReviewRequest;
import com.mgrunt.movies.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping(path="/create/{tmdbId}")
    public ResponseEntity<ReviewDto> createReview(
            @RequestBody ReviewRequest reviewRequest,
            @PathVariable Long tmdbId,
            Authentication authentication){
        ReviewDto review = reviewService.createReview(tmdbId, reviewRequest, authentication);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }


    @GetMapping(path = "/{tmdbId}")
    public ResponseEntity<List<ReviewDto>> getReviewsForMovie(
            @PathVariable Long tmdbId) {

        return new ResponseEntity<>(reviewService.getReviewsForMovie(tmdbId), HttpStatus.OK);
    }
}
