package com.mgrunt.movies.controllers;

import com.mgrunt.movies.domain.dtos.AddMovieRequest;
import com.mgrunt.movies.domain.dtos.MovieDto;
import com.mgrunt.movies.domain.dtos.UserDto;
import com.mgrunt.movies.domain.dtos.UserWatchListResponse;
import com.mgrunt.movies.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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

    @PatchMapping(path = "/watchlist")
    public ResponseEntity<Void> addToWatchlist(
            @RequestBody AddMovieRequest addMovieRequest,
            Authentication authentication) {

        userService.addToWatchlist(addMovieRequest, authentication);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/watchlist")
    public ResponseEntity<UserWatchListResponse> getWatchlist(Authentication authentication) {
        return new ResponseEntity<>(
                userService.getWatchlist(authentication),
                HttpStatus.OK
        );
    }
}
