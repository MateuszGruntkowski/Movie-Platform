package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.review.ReviewDto;
import com.mgrunt.movies.domain.dtos.review.ReviewRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;


public interface ReviewService {

    Page<ReviewDto> getReviewsForMovie(Long tmdbId, Pageable pageable);

    ReviewDto createReview(Long tmdbId, ReviewRequest reviewRequest, Authentication authentication);

}
