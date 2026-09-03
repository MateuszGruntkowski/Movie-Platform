package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.repositories.MovieRepository;
import com.mgrunt.movies.testsupport.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link MovieSyncServiceImpl}.
 * <p>
 * MovieRepository and MovieDataAssembler are mocked, so no real
 * persistence or TMDB-fetching logic runs. Note that this test class
 * must live in the same package as MovieSyncServiceImpl, because
 * MovieDataAssembler is package-private.
 */
@ExtendWith(MockitoExtension.class)
class MovieSyncServiceImplTest {

    private static final Long TMDB_ID = 550L;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieDataAssembler movieDataAssembler;

    private MovieSyncServiceImpl movieSyncService;

    @BeforeEach
    void setUp() {
        movieSyncService = new MovieSyncServiceImpl(movieRepository, movieDataAssembler);
    }

    @Nested
    class GetOrCreatePersistedMovie {

        @Test
        void assemblesAndSaves_whenMovieDoesNotExist() {
            Movie savedMovie = TestFixtures.aMovie();

            when(movieRepository.findByTmdbId(TMDB_ID)).thenReturn(Optional.empty());
            when(movieRepository.save(any(Movie.class))).thenReturn(savedMovie);

            Movie result = movieSyncService.getOrCreatePersistedMovie(TMDB_ID);

            verify(movieDataAssembler).assemble(any(Movie.class), eq(TMDB_ID));
            verify(movieRepository).save(any(Movie.class));
            assertThat(result).isSameAs(savedMovie);
        }

        @Test
        void assemblesAndSaves_whenExistingMovieIsStale() {
            Movie staleMovie = TestFixtures.aMovie();
            staleMovie.setUpdatedAt(LocalDateTime.now().minusHours(25));
            Movie savedMovie = TestFixtures.aMovie();

            when(movieRepository.findByTmdbId(TMDB_ID)).thenReturn(Optional.of(staleMovie));
            when(movieRepository.save(staleMovie)).thenReturn(savedMovie);

            Movie result = movieSyncService.getOrCreatePersistedMovie(TMDB_ID);

            verify(movieDataAssembler).assemble(staleMovie, TMDB_ID);
            verify(movieRepository).save(staleMovie);
            assertThat(result).isSameAs(savedMovie);
        }

        @Test
        void assemblesAndSaves_whenExistingMovieHasNeverBeenUpdated() {
            Movie neverUpdatedMovie = TestFixtures.aMovie();
            neverUpdatedMovie.setUpdatedAt(null);
            Movie savedMovie = TestFixtures.aMovie();

            when(movieRepository.findByTmdbId(TMDB_ID)).thenReturn(Optional.of(neverUpdatedMovie));
            when(movieRepository.save(neverUpdatedMovie)).thenReturn(savedMovie);

            Movie result = movieSyncService.getOrCreatePersistedMovie(TMDB_ID);

            verify(movieDataAssembler).assemble(neverUpdatedMovie, TMDB_ID);
            assertThat(result).isSameAs(savedMovie);
        }

        @Test
        void returnsExistingMovieUnchanged_whenNotStale() {
            Movie freshMovie = TestFixtures.aMovie();
            freshMovie.setUpdatedAt(LocalDateTime.now().minusHours(1));

            when(movieRepository.findByTmdbId(TMDB_ID)).thenReturn(Optional.of(freshMovie));

            Movie result = movieSyncService.getOrCreatePersistedMovie(TMDB_ID);

            verifyNoInteractions(movieDataAssembler);
            verify(movieRepository, never()).save(any(Movie.class));
            assertThat(result).isSameAs(freshMovie);
        }
    }

    @Nested
    class GetMovieForDisplay {

        @Test
        void assemblesTransientMovie_withoutSaving_whenMovieDoesNotExist() {
            Movie assembledMovie = TestFixtures.aMovie();

            when(movieRepository.findByTmdbId(TMDB_ID)).thenReturn(Optional.empty());
            when(movieDataAssembler.assemble(any(Movie.class), eq(TMDB_ID))).thenReturn(assembledMovie);

            Movie result = movieSyncService.getMovieForDisplay(TMDB_ID);

            verify(movieDataAssembler).assemble(any(Movie.class), eq(TMDB_ID));
            verify(movieRepository, never()).save(any(Movie.class));
            assertThat(result).isSameAs(assembledMovie);
        }

        @Test
        void assemblesAndSaves_whenExistingMovieIsStale() {
            Movie staleMovie = TestFixtures.aMovie();
            staleMovie.setUpdatedAt(LocalDateTime.now().minusHours(25));

            when(movieRepository.findByTmdbId(TMDB_ID)).thenReturn(Optional.of(staleMovie));
            when(movieDataAssembler.assemble(staleMovie, TMDB_ID)).thenReturn(staleMovie);

            Movie result = movieSyncService.getMovieForDisplay(TMDB_ID);

            verify(movieDataAssembler).assemble(staleMovie, TMDB_ID);
            verify(movieRepository).save(staleMovie);
            assertThat(result).isSameAs(staleMovie);
        }

        @Test
        void returnsExistingMovieUnchanged_whenNotStale() {
            Movie freshMovie = TestFixtures.aMovie();
            freshMovie.setUpdatedAt(LocalDateTime.now().minusHours(1));

            when(movieRepository.findByTmdbId(TMDB_ID)).thenReturn(Optional.of(freshMovie));

            Movie result = movieSyncService.getMovieForDisplay(TMDB_ID);

            verifyNoInteractions(movieDataAssembler);
            verify(movieRepository, never()).save(any(Movie.class));
            assertThat(result).isSameAs(freshMovie);
        }
    }
}