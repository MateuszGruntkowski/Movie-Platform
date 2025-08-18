package com.mgrunt.movies.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between {min} and {max} characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 30, message = "Password must be between {min} and {max} characters")
    private String password;

}
