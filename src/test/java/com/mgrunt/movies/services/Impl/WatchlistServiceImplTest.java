package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.movie.MovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.watchlist.UserWatchListResponse;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.domain.entities.User;
import com.mgrunt.movies.mappers.MovieMapper;
import com.mgrunt.movies.repositories.UserRepository;
import com.mgrunt.movies.services.MovieService;
import com.mgrunt.movies.testsupport.TestFixtures;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link WatchlistServiceImpl}.
 * <p>
 * UserRepository, MovieService and MovieMapper are mocked, so no database
 * or external service is touched. Authentication is mocked to simulate the
 * currently logged-in user.
 */
@ExtendWith(MockitoExtension.class)
class WatchlistServiceImplTest {

    private static final Long TMDB_ID = 550L;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MovieService movieService;

    @Mock
    private MovieMapper movieMapper;

    @Mock
    private Authentication authentication;

    private WatchlistServiceImpl watchlistService;

    @BeforeEach
    void setUp() {
        watchlistService = new WatchlistServiceImpl(userRepository, movieService, movieMapper);
    }

    // ---------------------------------------------------------------
    // getWatchlist
    // ---------------------------------------------------------------

    @Nested
    class GetWatchlist {

        @Test
        void returnsMappedWatchedAndToWatchMovies() {
            Movie watchedMovie = TestFixtures.aMovie();
            Movie toWatchMovie = TestFixtures.aMovie();

            User user = userWith(Set.of(watchedMovie), Set.of(toWatchMovie));

            MovieDetailsResponse watchedResponse = mock(MovieDetailsResponse.class);
            MovieDetailsResponse toWatchResponse = mock(MovieDetailsResponse.class);

            when(authentication.getName()).thenReturn(TestFixtures.USERNAME);
            when(userRepository.findByUsername(TestFixtures.USERNAME)).thenReturn(Optional.of(user));
            when(movieMapper.toMovieDetailsResponse(watchedMovie)).thenReturn(watchedResponse);
            when(movieMapper.toMovieDetailsResponse(toWatchMovie)).thenReturn(toWatchResponse);

            UserWatchListResponse result = watchlistService.getWatchlist(authentication);

            assertThat(result.getMoviesWatched()).containsExactly(watchedResponse);
            assertThat(result.getMoviesToWatch()).containsExactly(toWatchResponse);
        }

        @Test
        void throwsEntityNotFoundException_whenUserDoesNotExist() {
            when(authentication.getName()).thenReturn(TestFixtures.USERNAME);
            when(userRepository.findByUsername(TestFixtures.USERNAME)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> watchlistService.getWatchlist(authentication))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("User not found");

            verifyNoInteractions(movieMapper);
        }
    }

    // ---------------------------------------------------------------
    // toggleMovie
    // ---------------------------------------------------------------

    @Nested
    class ToggleMovie {

        @Test
        void addsMovieToWatched_whenNotPresentInEitherList() {
            Movie movie = TestFixtures.aMovie();
            User user = userWith(new HashSet<>(), new HashSet<>());
            stubUserAndMovie(user, movie);

            watchlistService.toggleMovie(TMDB_ID, "watched", authentication);

            assertThat(user.getMoviesWatched()).containsExactly(movie);
            assertThat(user.getMoviesToWatch()).isEmpty();
            verify(userRepository).save(user);
        }

        @Test
        void removesMovieFromWatched_whenAlreadyPresent() {
            Movie movie = TestFixtures.aMovie();
            User user = userWith(new HashSet<>(Set.of(movie)), new HashSet<>());
            stubUserAndMovie(user, movie);

            watchlistService.toggleMovie(TMDB_ID, "watched", authentication);

            assertThat(user.getMoviesWatched()).isEmpty();
            verify(userRepository).save(user);
        }

        @Test
        void movesMovieFromToWatchToWatched() {
            Movie movie = TestFixtures.aMovie();
            User user = userWith(new HashSet<>(), new HashSet<>(Set.of(movie)));
            stubUserAndMovie(user, movie);

            watchlistService.toggleMovie(TMDB_ID, "watched", authentication);

            assertThat(user.getMoviesWatched()).containsExactly(movie);
            assertThat(user.getMoviesToWatch()).isEmpty();
            verify(userRepository).save(user);
        }

        @Test
        void addsMovieToToWatch_whenNotPresentInEitherList() {
            Movie movie = TestFixtures.aMovie();
            User user = userWith(new HashSet<>(), new HashSet<>());
            stubUserAndMovie(user, movie);

            watchlistService.toggleMovie(TMDB_ID, "toWatch", authentication);

            assertThat(user.getMoviesToWatch()).containsExactly(movie);
            assertThat(user.getMoviesWatched()).isEmpty();
            verify(userRepository).save(user);
        }

        @Test
        void removesMovieFromToWatch_whenAlreadyPresent() {
            Movie movie = TestFixtures.aMovie();
            User user = userWith(new HashSet<>(), new HashSet<>(Set.of(movie)));
            stubUserAndMovie(user, movie);

            watchlistService.toggleMovie(TMDB_ID, "toWatch", authentication);

            assertThat(user.getMoviesToWatch()).isEmpty();
            verify(userRepository).save(user);
        }

        @Test
        void movesMovieFromWatchedToToWatch() {
            Movie movie = TestFixtures.aMovie();
            User user = userWith(new HashSet<>(Set.of(movie)), new HashSet<>());
            stubUserAndMovie(user, movie);

            watchlistService.toggleMovie(TMDB_ID, "toWatch", authentication);

            assertThat(user.getMoviesToWatch()).containsExactly(movie);
            assertThat(user.getMoviesWatched()).isEmpty();
            verify(userRepository).save(user);
        }

        @Test
        void listTypeIsCaseInsensitive() {
            Movie movie = TestFixtures.aMovie();
            User user = userWith(new HashSet<>(), new HashSet<>());
            stubUserAndMovie(user, movie);

            watchlistService.toggleMovie(TMDB_ID, "WaTcHeD", authentication);

            assertThat(user.getMoviesWatched()).containsExactly(movie);
            verify(userRepository).save(user);
        }

        @Test
        void throwsIllegalArgumentException_forInvalidListType() {
            Movie movie = TestFixtures.aMovie();
            User user = userWith(new HashSet<>(), new HashSet<>());
            stubUserAndMovie(user, movie);

            assertThatThrownBy(() -> watchlistService.toggleMovie(TMDB_ID, "invalid", authentication))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid listType: invalid");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void throwsEntityNotFoundException_whenUserDoesNotExist() {
            when(authentication.getName()).thenReturn(TestFixtures.USERNAME);
            when(userRepository.findByUsername(TestFixtures.USERNAME)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> watchlistService.toggleMovie(TMDB_ID, "watched", authentication))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("User not found");

            verifyNoInteractions(movieService);
            verify(userRepository, never()).save(any(User.class));
        }

        /**
         * Stubs authentication + repository lookup so that {@code user} is
         * returned for the logged-in user, and {@code movie} is returned for
         * TMDB_ID.
         */
        private void stubUserAndMovie(User user, Movie movie) {
            when(authentication.getName()).thenReturn(TestFixtures.USERNAME);
            when(userRepository.findByUsername(TestFixtures.USERNAME)).thenReturn(Optional.of(user));
            when(movieService.getMovie(TMDB_ID)).thenReturn(movie);
        }
    }

    /**
     * A user built on top of {@link TestFixtures#aUser()}, with its
     * watched/to-watch collections overridden for the scenario under test.
     */
    private static User userWith(Set<Movie> watched, Set<Movie> toWatch) {
        User user = TestFixtures.aUser();
        user.setMoviesWatched(watched);
        user.setMoviesToWatch(toWatch);
        return user;
    }
}