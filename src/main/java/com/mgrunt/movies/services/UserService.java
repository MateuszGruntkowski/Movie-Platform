package com.mgrunt.movies.services;
import com.mgrunt.movies.domain.dtos.UserDto;
import org.springframework.security.core.Authentication;

public interface UserService {
    UserDto getUser(Authentication authentication);
}
