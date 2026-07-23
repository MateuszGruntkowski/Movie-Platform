package com.mgrunt.movies.controllers;

import com.mgrunt.movies.Security.CustomUserDetails;
import com.mgrunt.movies.domain.dtos.profile.ProfileRatingDto;
import com.mgrunt.movies.domain.dtos.profile.ProfileReviewDto;
import com.mgrunt.movies.domain.dtos.profile.UpdateAvatarRequest;
import com.mgrunt.movies.domain.dtos.user.UserDto;
import com.mgrunt.movies.domain.dtos.profile.UserProfileResponse;
import com.mgrunt.movies.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser(Authentication authentication) {
        return new ResponseEntity<>(
                userService.getUser(authentication),
                HttpStatus.OK
        );
    }

//    @GetMapping("/me/profile")
//    public ResponseEntity<UserProfileResponse> getMyProfile(
//            @AuthenticationPrincipal CustomUserDetails userDetails
//    ){
//        return ResponseEntity.ok(userService.getUserProfile(userDetails.getId()));
//    }

    @PutMapping("/me/avatar")
    public ResponseEntity<UserProfileResponse> updateAvatar(
            @RequestBody UpdateAvatarRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        UserProfileResponse response = userService.updateAvatar(userDetails.getId(), request.avatarPath());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable String username
    ){
        UserProfileResponse response = userService.getUserProfile(username);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/{username}/ratings")
    public ResponseEntity<Page<ProfileRatingDto>> getUserRatings(
            @PathVariable String username,
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(defaultValue = "newest") String sort
    ){
        return ResponseEntity.ok(userService.getUserRatings(username, pageable, sort));
    }

    @GetMapping("/{username}/reviews")
    public ResponseEntity<Page<ProfileReviewDto>> getUserReviews(
            @PathVariable String username,
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(defaultValue = "newest") String sort

    ){
        return ResponseEntity.ok(userService.getUserReviews(username, pageable, sort));
    }
}
