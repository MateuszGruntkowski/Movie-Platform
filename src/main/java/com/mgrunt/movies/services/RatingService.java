package com.mgrunt.movies.services;

import com.mgrunt.movies.Security.CustomUserDetails;
import com.mgrunt.movies.domain.dtos.rating.RatingRequest;
import com.mgrunt.movies.domain.dtos.rating.RatingResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.UUID;

public interface RatingService {
    Optional<RatingResponse> getUserRatingForMovie(Long tmdbId, UUID authorID);

    RatingResponse rateMovie(Long tmdbID, UUID authorId, @Valid Integer ratingValue);
}
