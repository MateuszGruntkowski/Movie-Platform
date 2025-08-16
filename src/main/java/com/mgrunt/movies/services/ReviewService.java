package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.ReviewDto;
import org.springframework.security.core.Authentication;

import java.security.Principal;

public interface ReviewService {
    ReviewDto createReview(String imdbId, String reviewBody, Authentication authentication);
}
