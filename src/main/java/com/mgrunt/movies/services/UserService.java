package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.profile.ProfileRatingDto;
import com.mgrunt.movies.domain.dtos.profile.ProfileReviewDto;
import com.mgrunt.movies.domain.dtos.user.UserDto;
import com.mgrunt.movies.domain.dtos.profile.UserProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserDto getUser(UUID userId);

    UserProfileResponse updateAvatar(UUID id, String avatarPath);

    UserProfileResponse getUserProfile(String username);

    Page<ProfileReviewDto> getUserReviews(String username, Pageable pageable, String sort);

    Page<ProfileRatingDto> getUserRatings(String username, Pageable pageable, String sort);
}
