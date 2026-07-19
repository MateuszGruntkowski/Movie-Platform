package com.mgrunt.movies.domain.dtos.profile;

import com.mgrunt.movies.domain.dtos.rating.RatingDto;
import com.mgrunt.movies.domain.dtos.review.ReviewDto;
import lombok.Builder;

import java.util.List;

@Builder
public record UserProfileResponse(
        String username,
        String avatarPath,
        double avgRating,
        int ratingsCount,
        int reviewsCount,
        int moviesWatchedCount,
        int moviesToWatchCount,
        List<ProfileReviewDto> reviews,
        List<ProfileRatingDto> ratings
) {
}
