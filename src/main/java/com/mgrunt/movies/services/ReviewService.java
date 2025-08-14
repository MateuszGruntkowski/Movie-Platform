package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.documents.Review;
import com.mgrunt.movies.domain.dtos.ReviewDto;

import java.security.Principal;

public interface ReviewService {
    ReviewDto createReview(String imdbId, String reviewBody, Principal principal);
}
