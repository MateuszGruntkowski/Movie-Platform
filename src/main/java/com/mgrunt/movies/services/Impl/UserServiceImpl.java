package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.user.UserDto;
import com.mgrunt.movies.domain.entities.User;
import com.mgrunt.movies.mappers.UserMapper;
import com.mgrunt.movies.repositories.UserRepository;
import com.mgrunt.movies.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto getUser(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsernameWithDetails(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return userMapper.toDto(user);
    }
}
