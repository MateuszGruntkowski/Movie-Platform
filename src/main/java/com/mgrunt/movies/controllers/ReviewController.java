package com.mgrunt.movies.controllers;

import com.mgrunt.movies.Security.CustomUserDetails;
import com.mgrunt.movies.domain.dtos.review.ReviewDto;
import com.mgrunt.movies.domain.dtos.review.ReviewRequest;
import com.mgrunt.movies.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
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
    public ResponseEntity<Page<ReviewDto>> getReviewsForMovie(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable Long tmdbId) {

        return new ResponseEntity<>(reviewService.getReviewsForMovie(tmdbId, pageable), HttpStatus.OK);
    }

    @DeleteMapping(path="/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        reviewService.deleteReview(reviewId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
