package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.movie.WatchlistMovieResponse;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.domain.entities.User;
import com.mgrunt.movies.mappers.MovieMapper;
import com.mgrunt.movies.repositories.MovieRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link WatchlistServiceImpl}.
 * <p>
 * UserRepository, MovieRepository, MovieService and MovieMapper are mocked,
 * so no database or external service is touched.
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
    private MovieRepository movieRepository;

    private WatchlistServiceImpl watchlistService;

    @BeforeEach
    void setUp() {
        watchlistService = new WatchlistServiceImpl(userRepository, movieService, movieMapper, movieRepository);
    }

    // ---------------------------------------------------------------
    // getMoviesToWatch
    // ---------------------------------------------------------------

    @Nested
    class GetMoviesToWatch {

        private final Pageable pageable = PageRequest.of(0, 10);

        @Test
        void returnsMappedPage_whenUserExists() {
            Movie movie = TestFixtures.aMovie();
            WatchlistMovieResponse response = mock(WatchlistMovieResponse.class);
            Page<Movie> moviePage = new PageImpl<>(List.of(movie));

            when(userRepository.existsById(TestFixtures.USER_ID)).thenReturn(true);
            when(movieRepository.findMoviesToWatchByUserId(TestFixtures.USER_ID, pageable)).thenReturn(moviePage);
            when(movieMapper.toWatchlistMovieResponse(movie)).thenReturn(response);

            Page<WatchlistMovieResponse> result = watchlistService.getMoviesToWatch(TestFixtures.USER_ID, pageable);

            assertThat(result.getContent()).containsExactly(response);
        }

        @Test
        void throwsEntityNotFoundException_whenUserDoesNotExist() {
            when(userRepository.existsById(TestFixtures.USER_ID)).thenReturn(false);

            assertThatThrownBy(() -> watchlistService.getMoviesToWatch(TestFixtures.USER_ID, pageable))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("User not found");

            verifyNoInteractions(movieRepository, movieMapper);
        }
    }

    // ---------------------------------------------------------------
    // getMoviesWatched
    // ---------------------------------------------------------------

    @Nested
    class GetMoviesWatched {

        private final Pageable pageable = PageRequest.of(0, 10);

        @Test
        void returnsMappedPage_whenUserExists() {
            Movie movie = TestFixtures.aMovie();
            WatchlistMovieResponse response = mock(WatchlistMovieResponse.class);
            Page<Movie> moviePage = new PageImpl<>(List.of(movie));

            when(userRepository.existsById(TestFixtures.USER_ID)).thenReturn(true);
            when(movieRepository.findMoviesWatchedByUserId(TestFixtures.USER_ID, pageable)).thenReturn(moviePage);
            when(movieMapper.toWatchlistMovieResponse(movie)).thenReturn(response);

            Page<WatchlistMovieResponse> result = watchlistService.getMoviesWatched(TestFixtures.USER_ID, pageable);

            assertThat(result.getContent()).containsExactly(response);
        }

        @Test
        void throwsEntityNotFoundException_whenUserDoesNotExist() {
            when(userRepository.existsById(TestFixtures.USER_ID)).thenReturn(false);

            assertThatThrownBy(() -> watchlistService.getMoviesWatched(TestFixtures.USER_ID, pageable))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("User not found");

            verifyNoInteractions(movieRepository, movieMapper);
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

            watchlistService.toggleMovie(TMDB_ID, "watched", TestFixtures.USER_ID);

            assertThat(user.getMoviesWatched()).containsExactly(movie);
            assertThat(user.getMoviesToWatch()).isEmpty();
            verify(userRepository).save(user);
        }

        @Test
        void removesMovieFromWatched_whenAlreadyPresent() {
            Movie movie = TestFixtures.aMovie();
            User user = userWith(new HashSet<>(Set.of(movie)), new HashSet<>());
            stubUserAndMovie(user, movie);

            watchlistService.toggleMovie(TMDB_ID, "watched", TestFixtures.USER_ID);

            assertThat(user.getMoviesWatched()).isEmpty();
            verify(userRepository).save(user);
        }

        @Test
        void movesMovieFromToWatchToWatched() {
            Movie movie = TestFixtures.aMovie();
            User user = userWith(new HashSet<>(), new HashSet<>(Set.of(movie)));
            stubUserAndMovie(user, movie);

            watchlistService.toggleMovie(TMDB_ID, "watched", TestFixtures.USER_ID);

            assertThat(user.getMoviesWatched()).containsExactly(movie);
            assertThat(user.getMoviesToWatch()).isEmpty();
            verify(userRepository).save(user);
        }

        @Test
        void addsMovieToToWatch_whenNotPresentInEitherList() {
            Movie movie = TestFixtures.aMovie();
            User user = userWith(new HashSet<>(), new HashSet<>());
            stubUserAndMovie(user, movie);

            watchlistService.toggleMovie(TMDB_ID, "toWatch", TestFixtures.USER_ID);

            assertThat(user.getMoviesToWatch()).containsExactly(movie);
            assertThat(user.getMoviesWatched()).isEmpty();
            verify(userRepository).save(user);
        }

        @Test
        void removesMovieFromToWatch_whenAlreadyPresent() {
            Movie movie = TestFixtures.aMovie();
            User user = userWith(new HashSet<>(), new HashSet<>(Set.of(movie)));
            stubUserAndMovie(user, movie);

            watchlistService.toggleMovie(TMDB_ID, "toWatch", TestFixtures.USER_ID);

            assertThat(user.getMoviesToWatch()).isEmpty();
            verify(userRepository).save(user);
        }

        @Test
        void movesMovieFromWatchedToToWatch() {
            Movie movie = TestFixtures.aMovie();
            User user = userWith(new HashSet<>(Set.of(movie)), new HashSet<>());
            stubUserAndMovie(user, movie);

            watchlistService.toggleMovie(TMDB_ID, "toWatch", TestFixtures.USER_ID);

            assertThat(user.getMoviesToWatch()).containsExactly(movie);
            assertThat(user.getMoviesWatched()).isEmpty();
            verify(userRepository).save(user);
        }

        @Test
        void listTypeIsCaseInsensitive() {
            Movie movie = TestFixtures.aMovie();
            User user = userWith(new HashSet<>(), new HashSet<>());
            stubUserAndMovie(user, movie);

            watchlistService.toggleMovie(TMDB_ID, "WaTcHeD", TestFixtures.USER_ID);

            assertThat(user.getMoviesWatched()).containsExactly(movie);
            verify(userRepository).save(user);
        }

        @Test
        void throwsIllegalArgumentException_forInvalidListType() {
            Movie movie = TestFixtures.aMovie();
            User user = userWith(new HashSet<>(), new HashSet<>());
            stubUserAndMovie(user, movie);

            assertThatThrownBy(() -> watchlistService.toggleMovie(TMDB_ID, "invalid", TestFixtures.USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid listType: invalid");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void throwsEntityNotFoundException_whenUserDoesNotExist() {
            when(userRepository.findById(TestFixtures.USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> watchlistService.toggleMovie(TMDB_ID, "watched", TestFixtures.USER_ID))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("User not found");

            verifyNoInteractions(movieService);
            verify(userRepository, never()).save(any(User.class));
        }

        /**
         * Stubs the repository lookup so that {@code user} is returned for
         * TestFixtures.USER_ID, and {@code movie} is returned for TMDB_ID.
         */
        private void stubUserAndMovie(User user, Movie movie) {
            when(userRepository.findById(TestFixtures.USER_ID)).thenReturn(Optional.of(user));
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