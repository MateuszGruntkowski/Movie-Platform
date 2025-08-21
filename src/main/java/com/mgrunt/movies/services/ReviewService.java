package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.ReviewDto;
import com.mgrunt.movies.domain.dtos.ReviewRequest;
import org.springframework.security.core.Authentication;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

public interface ReviewService {
    ReviewDto createReview(String imdbId, String reviewBody, Authentication authentication);

    List<ReviewDto> getReviewsForMovie(Long tmdbId);

    ReviewDto createReview(ReviewRequest reviewRequest, Authentication authentication);
}
