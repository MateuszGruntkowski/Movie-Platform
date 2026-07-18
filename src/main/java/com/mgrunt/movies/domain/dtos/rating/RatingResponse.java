package com.mgrunt.movies.domain.dtos.rating;

import java.util.UUID;

public record RatingResponse(
        UUID id,
        Integer rating
) {
}
