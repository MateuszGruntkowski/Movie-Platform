package com.mgrunt.movies.domain.dtos.profile;

import com.mgrunt.movies.domain.dtos.movie.UserProfileMovieDto;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ProfileReviewDto(
        UUID id,
        String body,
        LocalDateTime createdAt,
        UserProfileMovieDto movie
) {
}
