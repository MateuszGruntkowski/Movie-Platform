package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.Security.CustomUserDetails;
import com.mgrunt.movies.domain.entities.User;
import com.mgrunt.movies.repositories.UserRepository;
import com.mgrunt.movies.testsupport.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AuthServiceImpl}.
 * <p>
 * AuthenticationManager, UserRepository and PasswordEncoder are mocked, so
 * no real authentication, database access, or hashing takes place.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private static final String USERNAME = TestFixtures.USERNAME;
    private static final String RAW_PASSWORD = "plainPassword123";
    private static final String ENCODED_PASSWORD = "encoded-password";

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(authenticationManager, userRepository, passwordEncoder);
    }

    // ---------------------------------------------------------------
    // authenticate
    // ---------------------------------------------------------------

    @Nested
    class Authenticate {

        @Test
        void returnsPrincipal_whenAuthenticationSucceeds() {
            UserDetails principal = mock(UserDetails.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(principal);

            UserDetails result = authService.authenticate(USERNAME, RAW_PASSWORD);

            assertThat(result).isSameAs(principal);
        }

        @Test
        void passesUsernameAndPasswordToAuthenticationManager() {
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(mock(UserDetails.class));

            authService.authenticate(USERNAME, RAW_PASSWORD);

            ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                    ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
            verify(authenticationManager).authenticate(captor.capture());
            assertThat(captor.getValue().getPrincipal()).isEqualTo(USERNAME);
            assertThat(captor.getValue().getCredentials()).isEqualTo(RAW_PASSWORD);
        }

        @Test
        void throwsBadCredentialsException_whenAuthenticationIsNotAuthenticated() {
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(false);

            assertThatThrownBy(() -> authService.authenticate(USERNAME, RAW_PASSWORD))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessageContaining("Authentication failed for user: " + USERNAME);
        }

        @Test
        void propagatesBadCredentialsException_whenAuthenticationManagerRejectsCredentials() {
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authService.authenticate(USERNAME, RAW_PASSWORD))
                    .isInstanceOf(BadCredentialsException.class);
        }
    }

    // ---------------------------------------------------------------
    // register
    // ---------------------------------------------------------------

    @Nested
    class Register {

        @Test
        void throwsIllegalArgumentException_whenUsernameAlreadyExists() {
            when(userRepository.existsByUsername(USERNAME)).thenReturn(true);

            assertThatThrownBy(() -> authService.register(USERNAME, RAW_PASSWORD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User with this name already exists");

            verifyNoInteractions(passwordEncoder);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void encodesPasswordAndSavesNewUser_whenUsernameIsAvailable() {
            User savedUser = TestFixtures.aUser();

            when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
            when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            UserDetails result = authService.register(USERNAME, RAW_PASSWORD);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            User userToSave = userCaptor.getValue();
            assertThat(userToSave.getUsername()).isEqualTo(USERNAME);
            assertThat(userToSave.getPassword()).isEqualTo(ENCODED_PASSWORD);

            // The returned UserDetails should wrap the *saved* user (the one
            // returned by the repository), not the pre-save instance.
            assertThat(result).isInstanceOf(CustomUserDetails.class);
            assertThat(((CustomUserDetails) result).getUser()).isSameAs(savedUser);
            assertThat(result.getUsername()).isEqualTo(savedUser.getUsername());
        }

        @Test
        void neverStoresTheRawPassword() {
            when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
            when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
            when(userRepository.save(any(User.class))).thenReturn(TestFixtures.aUser());

            authService.register(USERNAME, RAW_PASSWORD);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getPassword())
                    .isEqualTo(ENCODED_PASSWORD)
                    .isNotEqualTo(RAW_PASSWORD);
        }
    }
}