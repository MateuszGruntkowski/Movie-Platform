package com.mgrunt.movies.domain.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 20, message = "Username must be between {min} and {max} characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 30, message = "Password must be between {min} and {max} characters")
        String password

) {
}