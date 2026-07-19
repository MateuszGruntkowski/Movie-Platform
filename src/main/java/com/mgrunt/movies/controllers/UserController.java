package com.mgrunt.movies.controllers;

import com.mgrunt.movies.Security.CustomUserDetails;
import com.mgrunt.movies.domain.dtos.profile.UpdateAvatarRequest;
import com.mgrunt.movies.domain.dtos.user.UserDto;
import com.mgrunt.movies.domain.dtos.profile.UserProfileResponse;
import com.mgrunt.movies.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(path = "/me")
    public ResponseEntity<UserDto> getUser(Authentication authentication) {
        return new ResponseEntity<>(
                userService.getUser(authentication),
                HttpStatus.OK
        );
    }

    @GetMapping(path="/me/profile")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        return ResponseEntity.ok(userService.getUserProfile(userDetails.getId()));
    }

    @PutMapping("/me/avatar")
    public ResponseEntity<UserDto> updateAvatar(
            @RequestBody UpdateAvatarRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        UserDto userDto = userService.updateAvatar(userDetails.getId(), request.avatarPath());
        return ResponseEntity.ok(userDto);
    }

}
