package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.review.ReviewDto;
import com.mgrunt.movies.domain.dtos.review.ReviewRequest;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.domain.entities.Review;
import com.mgrunt.movies.domain.entities.User;
import com.mgrunt.movies.mappers.ReviewMapper;
import com.mgrunt.movies.repositories.ReviewRepository;
import com.mgrunt.movies.repositories.UserRepository;
import com.mgrunt.movies.services.MovieService;
import com.mgrunt.movies.testsupport.TestFixtures;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ReviewServiceImpl}.
 * <p>
 * ReviewRepository, UserRepository, ReviewMapper and MovieService are
 * mocked, so no real persistence, mapping, or movie-fetching logic runs.
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    private static final Long TMDB_ID = 550L;
    private static final UUID AUTHOR_ID = TestFixtures.USER_ID;
    private static final String REVIEW_BODY = "Great movie, loved the pacing.";

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private MovieService movieService;

    private ReviewServiceImpl reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewServiceImpl(reviewRepository, userRepository, reviewMapper, movieService);
    }

    // ---------------------------------------------------------------
    // getReviewsForMovie
    // ---------------------------------------------------------------

    @Nested
    class GetReviewsForMovie {

        @Test
        void returnsPageOfMappedDtos() {
            Pageable pageable = PageRequest.of(0, 10);
            Review review = Review.builder()
                    .body(REVIEW_BODY)
                    .build();
            Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);
            ReviewDto expectedDto = mock(ReviewDto.class);

            when(reviewRepository.findByMovieTmdbId(TMDB_ID, pageable)).thenReturn(reviewPage);
            when(reviewMapper.toDto(review)).thenReturn(expectedDto);

            Page<ReviewDto> result = reviewService.getReviewsForMovie(TMDB_ID, pageable);

            assertThat(result.getContent()).containsExactly(expectedDto);
            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        void returnsEmptyPage_whenMovieHasNoReviews() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Review> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(reviewRepository.findByMovieTmdbId(TMDB_ID, pageable)).thenReturn(emptyPage);

            Page<ReviewDto> result = reviewService.getReviewsForMovie(TMDB_ID, pageable);

            assertThat(result.getContent()).isEmpty();
            verifyNoInteractions(reviewMapper);
        }
    }

    // ---------------------------------------------------------------
    // createReview
    // ---------------------------------------------------------------

    @Nested
    class CreateReview {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t"})
        void throwsIllegalArgumentException_whenReviewBodyIsBlank(String blankBody) {
            ReviewRequest request = new ReviewRequest(blankBody);

            assertThatThrownBy(() -> reviewService.createReview(TMDB_ID, request, AUTHOR_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Review body cannot be null");

            verifyNoInteractions(userRepository, movieService, reviewRepository, reviewMapper);
        }

        @Test
        void throwsEntityNotFoundException_whenAuthorDoesNotExist() {
            ReviewRequest request = new ReviewRequest(REVIEW_BODY);

            when(userRepository.findById(AUTHOR_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.createReview(TMDB_ID, request, AUTHOR_ID))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("User not found");

            verifyNoInteractions(movieService, reviewRepository, reviewMapper);
        }

        @Test
        void savesReview_andReturnsMappedDto() {
            ReviewRequest request = new ReviewRequest(REVIEW_BODY);
            User author = TestFixtures.aUser();
            Movie movie = TestFixtures.aMovie();
            ReviewDto expectedDto = mock(ReviewDto.class);

            when(userRepository.findById(AUTHOR_ID)).thenReturn(Optional.of(author));
            when(movieService.getOrCreatePersistedMovie(TMDB_ID)).thenReturn(movie);
            when(reviewMapper.toDto(any(Review.class))).thenReturn(expectedDto);

            ReviewDto result = reviewService.createReview(TMDB_ID, request, AUTHOR_ID);

            ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
            verify(reviewRepository).save(reviewCaptor.capture());
            Review savedReview = reviewCaptor.getValue();

            assertThat(savedReview.getMovie()).isSameAs(movie);
            assertThat(savedReview.getAuthor()).isSameAs(author);
            assertThat(savedReview.getBody()).isEqualTo(REVIEW_BODY);

            assertThat(result).isSameAs(expectedDto);
        }
    }

    // ---------------------------------------------------------------
    // deleteReview
    // ---------------------------------------------------------------

    @Nested
    class DeleteReview {

        @Test
        void deletesReview_whenRequesterIsAuthor() {
            UUID reviewId = UUID.randomUUID();
            User author = TestFixtures.aUser(AUTHOR_ID, TestFixtures.USERNAME);
            Review review = Review.builder()
                    .author(author)
                    .build();

            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

            reviewService.deleteReview(reviewId, AUTHOR_ID);

            verify(reviewRepository).delete(review);
        }

        @Test
        void throwsEntityNotFoundException_whenReviewDoesNotExist() {
            UUID reviewId = UUID.randomUUID();

            when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.deleteReview(reviewId, AUTHOR_ID))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Review not found");

            verify(reviewRepository, never()).delete(any(Review.class));
        }

        @Test
        void throwsSecurityException_whenRequesterIsNotAuthor() {
            UUID reviewId = UUID.randomUUID();
            UUID differentAuthorId = UUID.randomUUID();
            User author = TestFixtures.aUser(differentAuthorId, "someone-else");
            Review review = Review.builder()
                    .author(author)
                    .build();

            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

            assertThatThrownBy(() -> reviewService.deleteReview(reviewId, AUTHOR_ID))
                    .isInstanceOf(SecurityException.class)
                    .hasMessageContaining("not authorized");

            verify(reviewRepository, never()).delete(any(Review.class));
        }
    }
}