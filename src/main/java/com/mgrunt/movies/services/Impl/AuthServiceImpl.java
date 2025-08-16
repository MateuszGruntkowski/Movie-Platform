package com.mgrunt.movies.services.Impl;
import com.mgrunt.movies.Security.CustomUserDetails;
import com.mgrunt.movies.domain.entities.User;
import com.mgrunt.movies.repositories.UserRepository;
import com.mgrunt.movies.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails authenticate(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        if (authentication.isAuthenticated()) {
            return (UserDetails) authentication.getPrincipal();
        } else {
            throw new RuntimeException("Authentication failed for user: " + username);
        }
    }

    @Override
    public UserDetails register(String username, String password) {

        if(userRepository.existsByUsername(username)){
            throw new RuntimeException("User with this name already exists");
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        return new CustomUserDetails(savedUser);
    }
}
