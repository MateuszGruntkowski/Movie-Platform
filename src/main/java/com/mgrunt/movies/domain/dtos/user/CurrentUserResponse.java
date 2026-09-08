package com.mgrunt.movies.domain.dtos.user;

public record CurrentUserResponse(
        String username,
        String avatarPath
) {
}