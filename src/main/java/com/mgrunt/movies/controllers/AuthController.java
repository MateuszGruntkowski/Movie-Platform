package com.mgrunt.movies.controllers;

import com.mgrunt.movies.Security.JwtUtil;
import com.mgrunt.movies.domain.dtos.AuthResponse;
import com.mgrunt.movies.domain.dtos.LoginRequest;
import com.mgrunt.movies.domain.dtos.RegisterRequest;
import com.mgrunt.movies.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping(path = "/login")
     public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
         UserDetails userDetails = authService.authenticate(
                 loginRequest.getUsername(),
                 loginRequest.getPassword());

         String token = jwtUtil.generateToken(userDetails);
         AuthResponse authResponse = AuthResponse.builder().token(token).build();

         return ResponseEntity.ok(authResponse);
     }

     @PostMapping(path = "/register")
         public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
             UserDetails userDetails = authService.register(
                     registerRequest.getUsername(),
                     registerRequest.getPassword());

             String token = jwtUtil.generateToken(userDetails);
             AuthResponse authResponse = AuthResponse.builder().token(token).build();

             return ResponseEntity.ok(authResponse);
         }
}
