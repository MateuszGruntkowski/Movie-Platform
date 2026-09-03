package com.mgrunt.movies.domain.dtos.review;

import java.util.UUID;

public record ReviewDto(
        UUID id,
        String body,
        String createdAt,
        String authorUsername,
        String authorAvatarPath,
        String authorId
) {
}