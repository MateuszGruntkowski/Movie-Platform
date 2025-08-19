package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.AddMovieRequest;
import com.mgrunt.movies.domain.dtos.MovieDto;
import com.mgrunt.movies.domain.dtos.UserDto;
import com.mgrunt.movies.domain.dtos.UserWatchListResponse;
import org.springframework.security.core.Authentication;

import java.util.Set;

public interface UserService {
    UserDto getUser(Authentication authentication);
}
