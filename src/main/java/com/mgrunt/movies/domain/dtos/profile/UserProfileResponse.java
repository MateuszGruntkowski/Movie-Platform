package com.mgrunt.movies.domain.dtos.profile;

import lombok.Builder;

@Builder
public record UserProfileResponse(
        String username,
        String avatarPath,
        double avgRating,
        int ratingsCount,
        int reviewsCount,
        int moviesWatchedCount,
        int moviesToWatchCount
) {
}
