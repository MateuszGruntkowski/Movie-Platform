package com.mgrunt.movies.domain.dtos.profile;

import com.mgrunt.movies.domain.dtos.movie.MovieSummaryDto;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ProfileReviewDto(
        UUID id,
        String body,
        String createdAt,
        MovieSummaryDto movie
) {
}
