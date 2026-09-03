package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.tmdb.TmdbGenreResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbMovieDetailsResponse;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.services.TmdbService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link MovieDataAssembler}.
 * <p>
 * TmdbService is mocked, so no real HTTP calls take place. Uses mocked
 * DTO accessor methods rather than constructing real records, so it
 * doesn't depend on the exact record component order.
 */
@ExtendWith(MockitoExtension.class)
class MovieDataAssemblerTest {

    private static final Long TMDB_ID = 550L;
    // Must match DEFAULT_BACKDROPS_LIMIT in MovieDataAssembler.
    private static final int BACKDROPS_LIMIT = 5;

    @Mock
    private TmdbService tmdbService;

    @Mock
    private TmdbMovieDetailsResponse tmdbMovie;

    private MovieDataAssembler movieDataAssembler;

    @BeforeEach
    void setUp() {
        movieDataAssembler = new MovieDataAssembler(tmdbService);

        when(tmdbMovie.imdbId()).thenReturn("tt0137523");
        when(tmdbMovie.title()).thenReturn("Fight Club");
        when(tmdbMovie.overview()).thenReturn("An insomniac office worker...");
        when(tmdbMovie.releaseDate()).thenReturn(LocalDate.of(1999, 10, 15));
        when(tmdbMovie.posterPath()).thenReturn("/poster.jpg");
        when(tmdbMovie.backdropPath()).thenReturn("/backdrop.jpg");
        when(tmdbMovie.voteAverage()).thenReturn(8.4);
        when(tmdbMovie.voteCount()).thenReturn(25000);
        when(tmdbMovie.popularity()).thenReturn(61.3);
        when(tmdbMovie.runtime()).thenReturn(139);
        when(tmdbMovie.originalLanguage()).thenReturn("en");
        when(tmdbMovie.adult()).thenReturn(false);
        when(tmdbMovie.budget()).thenReturn(63_000_000L);
        when(tmdbMovie.revenue()).thenReturn(100_853_753L);
        when(tmdbMovie.tagline()).thenReturn("Mischief. Mayhem. Soap.");
        when(tmdbMovie.genres()).thenReturn(List.of());

        when(tmdbService.fetchRawMovieDetails(TMDB_ID)).thenReturn(tmdbMovie);
        when(tmdbService.getTrailerUrl(TMDB_ID)).thenReturn("https://www.youtube.com/watch?v=abc123");
        when(tmdbService.getImages(TMDB_ID, BACKDROPS_LIMIT)).thenReturn(List.of("/bd1.jpg", "/bd2.jpg"));
    }

    @Test
    void copiesAllScalarFields_fromTmdbResponseToMovie() {
        Movie movie = new Movie();

        movieDataAssembler.assemble(movie, TMDB_ID);

        assertThat(movie.getTmdbId()).isEqualTo(TMDB_ID);
        assertThat(movie.getImdbId()).isEqualTo("tt0137523");
        assertThat(movie.getTitle()).isEqualTo("Fight Club");
        assertThat(movie.getOverview()).isEqualTo("An insomniac office worker...");
        assertThat(movie.getReleaseDate()).isEqualTo(LocalDate.of(1999, 10, 15));
        assertThat(movie.getPosterPath()).isEqualTo("/poster.jpg");
        assertThat(movie.getBackdropPath()).isEqualTo("/backdrop.jpg");
        assertThat(movie.getVoteAverage()).isEqualTo(8.4);
        assertThat(movie.getVoteCount()).isEqualTo(25000);
        assertThat(movie.getPopularity()).isEqualTo(61.3);
        assertThat(movie.getRuntime()).isEqualTo(139);
        assertThat(movie.getOriginalLanguage()).isEqualTo("en");
        assertThat(movie.getAdult()).isFalse();
        assertThat(movie.getBudget()).isEqualTo(63_000_000L);
        assertThat(movie.getRevenue()).isEqualTo(100_853_753L);
        assertThat(movie.getTagline()).isEqualTo("Mischief. Mayhem. Soap.");
        assertThat(movie.getTrailerUrl()).isEqualTo("https://www.youtube.com/watch?v=abc123");
    }

    @Test
    void setsUpdatedAt_toCurrentTime() {
        Movie movie = new Movie();
        LocalDateTime before = LocalDateTime.now();

        movieDataAssembler.assemble(movie, TMDB_ID);

        LocalDateTime after = LocalDateTime.now();
        assertThat(movie.getUpdatedAt()).isBetween(before, after);
    }

    @Test
    void returnsSameMovieInstance_thatWasPassedIn() {
        Movie movie = new Movie();

        Movie result = movieDataAssembler.assemble(movie, TMDB_ID);

        assertThat(result).isSameAs(movie);
    }

    @Test
    void replacesBackdrops_withImagesFromTmdbService_usingConfiguredLimit() {
        Movie movie = new Movie();
        movie.getBackdrops().add("/old-backdrop-to-be-cleared.jpg");

        movieDataAssembler.assemble(movie, TMDB_ID);

        assertThat(movie.getBackdrops()).containsExactly("/bd1.jpg", "/bd2.jpg");
        verify(tmdbService).getImages(TMDB_ID, BACKDROPS_LIMIT);
    }

    @Test
    void replacesGenres_withDistinctNamesFromTmdbResponse() {
        TmdbGenreResponse drama = mock(TmdbGenreResponse.class);
        TmdbGenreResponse thriller = mock(TmdbGenreResponse.class);
        TmdbGenreResponse dramaDuplicate = mock(TmdbGenreResponse.class);
        when(drama.name()).thenReturn("Drama");
        when(thriller.name()).thenReturn("Thriller");
        when(dramaDuplicate.name()).thenReturn("Drama");
        when(tmdbMovie.genres()).thenReturn(List.of(drama, thriller, dramaDuplicate));

        Movie movie = new Movie();
        movie.getGenres().add("Old genre to be cleared");

        movieDataAssembler.assemble(movie, TMDB_ID);

        assertThat(movie.getGenres()).containsExactly("Drama", "Thriller");
    }

    @Test
    void resultsInEmptyGenres_whenTmdbResponseHasNullGenres() {
        when(tmdbMovie.genres()).thenReturn(null);
        Movie movie = new Movie();

        movieDataAssembler.assemble(movie, TMDB_ID);

        assertThat(movie.getGenres()).isEmpty();
    }
}