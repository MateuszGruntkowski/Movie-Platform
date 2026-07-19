package com.mgrunt.movies.domain.dtos.rating;

import java.util.UUID;

public record RatingDto(
        UUID id,
        Integer rating
) {
}
