package com.mgrunt.movies.domain.dtos.movie;

import lombok.Builder;

import java.util.UUID;

@Builder
public record MovieSummaryDto(
        UUID id,
        Long tmdbId,
        String title,
        String posterPath
) {
}
