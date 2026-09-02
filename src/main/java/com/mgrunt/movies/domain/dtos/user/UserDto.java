package com.mgrunt.movies.domain.dtos.user;

import java.util.Set;

public record UserDto(
        String username,
        String avatarPath,
        Set<Long> moviesToWatchIds,
        Set<Long> moviesWatchedIds
) {
}