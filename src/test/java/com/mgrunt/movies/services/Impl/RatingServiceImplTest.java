package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.rating.RatingDto;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.domain.entities.Rating;
import com.mgrunt.movies.domain.entities.User;
import com.mgrunt.movies.mappers.RatingMapper;
import com.mgrunt.movies.repositories.RatingRepository;
import com.mgrunt.movies.repositories.UserRepository;
import com.mgrunt.movies.services.MovieService;
import com.mgrunt.movies.testsupport.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RatingServiceImpl}.
 * <p>
 * RatingRepository, RatingMapper, MovieService and UserRepository are
 * mocked, so no real persistence, mapping, or movie-fetching logic runs.
 */
@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {

    private static final Long TMDB_ID = 550L;
    private static final UUID AUTHOR_ID = TestFixtures.USER_ID;
    private static final Integer RATING_VALUE = 8;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RatingMapper ratingMapper;

    @Mock
    private MovieService movieService;

    @Mock
    private UserRepository userRepository;

    private RatingServiceImpl ratingService;

    @BeforeEach
    void setUp() {
        ratingService = new RatingServiceImpl(ratingRepository, ratingMapper, movieService, userRepository);
    }

    // ---------------------------------------------------------------
    // getUserRatingForMovie
    // ---------------------------------------------------------------

    @Nested
    class GetUserRatingForMovie {

        @Test
        void returnsMappedDto_whenRatingExists() {
            Rating existingRating = Rating.builder()
                    .rating(RATING_VALUE)
                    .build();
            RatingDto expectedDto = mock(RatingDto.class);

            when(ratingRepository.findByMovie_TmdbIdAndAuthorId(TMDB_ID, AUTHOR_ID))
                    .thenReturn(Optional.of(existingRating));
            when(ratingMapper.toRatingDto(existingRating)).thenReturn(expectedDto);

            Optional<RatingDto> result = ratingService.getUserRatingForMovie(TMDB_ID, AUTHOR_ID);

            assertThat(result).contains(expectedDto);
        }

        @Test
        void returnsEmptyOptional_whenRatingDoesNotExist() {
            when(ratingRepository.findByMovie_TmdbIdAndAuthorId(TMDB_ID, AUTHOR_ID))
                    .thenReturn(Optional.empty());

            Optional<RatingDto> result = ratingService.getUserRatingForMovie(TMDB_ID, AUTHOR_ID);

            assertThat(result).isEmpty();
            verifyNoInteractions(ratingMapper);
        }
    }

    // ---------------------------------------------------------------
    // rateMovie
    // ---------------------------------------------------------------

    @Nested
    class RateMovie {

        @Test
        void updatesRatingValue_whenRatingAlreadyExists() {
            Rating existingRating = Rating.builder()
                    .rating(3)
                    .build();
            Rating savedRating = Rating.builder()
                    .rating(RATING_VALUE)
                    .build();
            RatingDto expectedDto = mock(RatingDto.class);

            when(ratingRepository.findByMovie_TmdbIdAndAuthorId(TMDB_ID, AUTHOR_ID))
                    .thenReturn(Optional.of(existingRating));
            when(ratingRepository.save(existingRating)).thenReturn(savedRating);
            when(ratingMapper.toRatingDto(savedRating)).thenReturn(expectedDto);

            RatingDto result = ratingService.rateMovie(TMDB_ID, AUTHOR_ID, RATING_VALUE);

            assertThat(existingRating.getRating()).isEqualTo(RATING_VALUE);
            assertThat(result).isSameAs(expectedDto);

            // Update path must not touch movie lookup or user reference.
            verifyNoInteractions(movieService);
            verifyNoInteractions(userRepository);
            verify(ratingRepository).save(existingRating);
        }

        @Test
        void createsNewRating_whenNoneExistsYet() {
            Movie movie = TestFixtures.aMovie();
            User author = TestFixtures.aUser();
            Rating savedRating = Rating.builder()
                    .rating(RATING_VALUE)
                    .movie(movie)
                    .author(author)
                    .build();
            RatingDto expectedDto = mock(RatingDto.class);

            when(ratingRepository.findByMovie_TmdbIdAndAuthorId(TMDB_ID, AUTHOR_ID))
                    .thenReturn(Optional.empty());
            when(movieService.getOrCreatePersistedMovie(TMDB_ID)).thenReturn(movie);
            when(userRepository.getReferenceById(AUTHOR_ID)).thenReturn(author);
            when(ratingRepository.save(any(Rating.class))).thenReturn(savedRating);
            when(ratingMapper.toRatingDto(savedRating)).thenReturn(expectedDto);

            RatingDto result = ratingService.rateMovie(TMDB_ID, AUTHOR_ID, RATING_VALUE);

            ArgumentCaptor<Rating> ratingCaptor = ArgumentCaptor.forClass(Rating.class);
            verify(ratingRepository).save(ratingCaptor.capture());
            Rating ratingToSave = ratingCaptor.getValue();

            assertThat(ratingToSave.getRating()).isEqualTo(RATING_VALUE);
            assertThat(ratingToSave.getMovie()).isSameAs(movie);
            assertThat(ratingToSave.getAuthor()).isSameAs(author);
            assertThat(result).isSameAs(expectedDto);
        }

        @Test
        void fetchesMovieAndAuthor_beforeCreatingNewRating() {
            Movie movie = TestFixtures.aMovie();
            User author = TestFixtures.aUser();

            when(ratingRepository.findByMovie_TmdbIdAndAuthorId(TMDB_ID, AUTHOR_ID))
                    .thenReturn(Optional.empty());
            when(movieService.getOrCreatePersistedMovie(TMDB_ID)).thenReturn(movie);
            when(userRepository.getReferenceById(AUTHOR_ID)).thenReturn(author);
            when(ratingRepository.save(any(Rating.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(ratingMapper.toRatingDto(any(Rating.class))).thenReturn(mock(RatingDto.class));

            ratingService.rateMovie(TMDB_ID, AUTHOR_ID, RATING_VALUE);

            verify(movieService).getOrCreatePersistedMovie(TMDB_ID);
            verify(userRepository).getReferenceById(AUTHOR_ID);
        }
    }
}