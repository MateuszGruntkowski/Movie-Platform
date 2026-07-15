package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.review.ReviewDto;
import com.mgrunt.movies.domain.dtos.review.ReviewRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ReviewService {

    List<ReviewDto> getReviewsForMovie(Long tmdbId);

    ReviewDto createReview(Long tmdbId, ReviewRequest reviewRequest, Authentication authentication);

}
