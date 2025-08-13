package com.mgrunt.movies.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    UserDetails authenticate(String username, String password);
    UserDetails register(String username, String password);
}
