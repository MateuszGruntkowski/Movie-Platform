package com.mgrunt.movies.domain.dtos.movie;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserProfileMovieDto(
        UUID id,
        Long tmdbId,
        String title,
        String posterPath
) {
}
