package com.mgrunt.movies.controllers;

import com.mgrunt.movies.domain.dtos.ReviewDto;
import com.mgrunt.movies.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody Map<String, String> payload, Authentication authentication) {
        ReviewDto review = reviewService.createReview(payload.get("imdbId"), payload.get("reviewBody"), authentication);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }
}
