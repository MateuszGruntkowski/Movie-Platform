package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.user.UserDto;
import com.mgrunt.movies.domain.dtos.profile.UserProfileResponse;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface UserService {
    UserDto getUser(Authentication authentication);

    UserProfileResponse getUserProfile(UUID id);

    UserDto updateAvatar(UUID id, String avatarPath);
}
