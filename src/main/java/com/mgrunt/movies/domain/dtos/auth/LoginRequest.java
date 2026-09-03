package com.mgrunt.movies.domain.dtos.auth;

public record LoginRequest(
        String username,
        String password
) {
}