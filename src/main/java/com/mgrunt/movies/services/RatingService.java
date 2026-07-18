package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.rating.RatingResponse;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.UUID;

public interface RatingService {
    Optional<RatingResponse> getUserRatingForMovie(Long tmdbId, Authentication auth);
}
