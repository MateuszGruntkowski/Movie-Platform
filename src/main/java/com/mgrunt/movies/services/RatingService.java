package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.rating.RatingDto;
import jakarta.validation.Valid;

import java.util.Optional;
import java.util.UUID;

public interface RatingService {
    Optional<RatingDto> getUserRatingForMovie(Long tmdbId, UUID authorID);

    RatingDto rateMovie(Long tmdbID, UUID authorId, @Valid Integer ratingValue);
}
