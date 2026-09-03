package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.movie.MovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.movie.MovieSearchResponse;
import com.mgrunt.movies.domain.dtos.movie.TrendingMovieResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbSearchResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbTrendingMovieItemResponse;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.exceptions.MovieSearchException;
import com.mgrunt.movies.mappers.MovieMapper;
import com.mgrunt.movies.services.MovieSyncService;
import com.mgrunt.movies.services.TmdbService;
import com.mgrunt.movies.testsupport.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link MovieServiceImpl}.
 * <p>
 * TmdbService, MovieMapper and MovieSyncService are mocked, so no real
 * HTTP calls, mapping, or persistence logic runs.
 */
@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    private static final Long TMDB_ID = 550L;
    private static final String QUERY = "fight club";
    private static final int PAGE = 1;

    @Mock
    private TmdbService tmdbService;

    @Mock
    private MovieMapper movieMapper;

    @Mock
    private MovieSyncService movieSyncService;

    private MovieServiceImpl movieService;

    @BeforeEach
    void setUp() {
        movieService = new MovieServiceImpl(tmdbService, movieMapper, movieSyncService);
    }

    @Nested
    class GetTrendingMovies {

        @Test
        void mapsEachTmdbItemToTrendingMovieResponse_preservingOrder() {
            TmdbTrendingMovieItemResponse item1 = mock(TmdbTrendingMovieItemResponse.class);
            TmdbTrendingMovieItemResponse item2 = mock(TmdbTrendingMovieItemResponse.class);
            TrendingMovieResponse dto1 = mock(TrendingMovieResponse.class);
            TrendingMovieResponse dto2 = mock(TrendingMovieResponse.class);

            when(tmdbService.getTrendingMovies()).thenReturn(List.of(item1, item2));
            when(movieMapper.toTrendingMovie(item1)).thenReturn(dto1);
            when(movieMapper.toTrendingMovie(item2)).thenReturn(dto2);

            List<TrendingMovieResponse> result = movieService.getTrendingMovies();

            assertThat(result).containsExactly(dto1, dto2);
        }

        @Test
        void returnsEmptyList_whenTmdbServiceReturnsEmptyList() {
            when(tmdbService.getTrendingMovies()).thenReturn(List.of());

            List<TrendingMovieResponse> result = movieService.getTrendingMovies();

            assertThat(result).isEmpty();
            verifyNoInteractions(movieMapper);
        }
    }

    @Nested
    class GetMovieDetails {

        @Test
        void delegatesToMovieSyncService_andMapsResult() {
            Movie movie = TestFixtures.aMovie();
            MovieDetailsResponse expectedDto = mock(MovieDetailsResponse.class);

            when(movieSyncService.getMovieForDisplay(TMDB_ID)).thenReturn(movie);
            when(movieMapper.toMovieDetailsResponse(movie)).thenReturn(expectedDto);

            MovieDetailsResponse result = movieService.getMovieDetails(TMDB_ID);

            assertThat(result).isSameAs(expectedDto);
        }
    }

    @Nested
    class SearchMovies {

        @Test
        void delegatesToTmdbService_andMapsResult() {
            TmdbSearchResponse tmdbResponse = mock(TmdbSearchResponse.class);
            MovieSearchResponse expectedDto = mock(MovieSearchResponse.class);

            when(tmdbService.searchResult(QUERY, PAGE)).thenReturn(tmdbResponse);
            when(movieMapper.toMovieSearchResponse(tmdbResponse)).thenReturn(expectedDto);

            MovieSearchResponse result = movieService.searchMovies(QUERY, PAGE);

            assertThat(result).isSameAs(expectedDto);
        }

        @Test
        void wrapsTmdbServiceFailure_inMovieSearchException() {
            RuntimeException cause = new RuntimeException("TMDB unavailable");
            when(tmdbService.searchResult(QUERY, PAGE)).thenThrow(cause);

            assertThatThrownBy(() -> movieService.searchMovies(QUERY, PAGE))
                    .isInstanceOf(MovieSearchException.class)
                    .hasMessageContaining(QUERY)
                    .hasCause(cause);

            verifyNoInteractions(movieMapper);
        }

        @Test
        void wrapsMapperFailure_inMovieSearchException() {
            TmdbSearchResponse tmdbResponse = mock(TmdbSearchResponse.class);
            RuntimeException cause = new RuntimeException("mapping failed");

            when(tmdbService.searchResult(QUERY, PAGE)).thenReturn(tmdbResponse);
            when(movieMapper.toMovieSearchResponse(tmdbResponse)).thenThrow(cause);

            assertThatThrownBy(() -> movieService.searchMovies(QUERY, PAGE))
                    .isInstanceOf(MovieSearchException.class)
                    .hasCause(cause);
        }
    }

    @Nested
    class GetOrCreatePersistedMovie {

        @Test
        void delegatesDirectlyToMovieSyncService() {
            Movie expectedMovie = TestFixtures.aMovie();
            when(movieSyncService.getOrCreatePersistedMovie(TMDB_ID)).thenReturn(expectedMovie);

            Movie result = movieService.getOrCreatePersistedMovie(TMDB_ID);

            assertThat(result).isSameAs(expectedMovie);
        }
    }
}