package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.constants.AvatarConstants;
import com.mgrunt.movies.domain.dtos.profile.ProfileRatingDto;
import com.mgrunt.movies.domain.dtos.profile.ProfileReviewDto;
import com.mgrunt.movies.domain.dtos.profile.UserProfileResponse;
import com.mgrunt.movies.domain.dtos.user.UserDto;
import com.mgrunt.movies.domain.entities.Rating;
import com.mgrunt.movies.domain.entities.Review;
import com.mgrunt.movies.domain.entities.User;
import com.mgrunt.movies.mappers.RatingMapper;
import com.mgrunt.movies.mappers.ReviewMapper;
import com.mgrunt.movies.mappers.UserMapper;
import com.mgrunt.movies.repositories.RatingRepository;
import com.mgrunt.movies.repositories.ReviewRepository;
import com.mgrunt.movies.repositories.UserRepository;
import com.mgrunt.movies.testsupport.TestFixtures;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserServiceImpl}.
 * <p>
 * All repositories and mappers are mocked, so no database access happens.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RatingMapper ratingMapper;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, userMapper, reviewRepository, reviewMapper, ratingRepository, ratingMapper);
    }

    // ---------------------------------------------------------------
    // getUser
    // ---------------------------------------------------------------

    @Nested
    class GetUser {

        @Test
        void returnsMappedUserDto_whenUserExists() {
            User user = TestFixtures.aUser();
            UserDto userDto = mock(UserDto.class);

            when(userRepository.findById(TestFixtures.USER_ID)).thenReturn(Optional.of(user));
            when(userMapper.toDto(user)).thenReturn(userDto);

            UserDto result = userService.getUser(TestFixtures.USER_ID);

            assertThat(result).isSameAs(userDto);
        }

        @Test
        void throwsEntityNotFoundException_whenUserDoesNotExist() {
            when(userRepository.findById(TestFixtures.USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUser(TestFixtures.USER_ID))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("User not found");

            verifyNoInteractions(userMapper);
        }
    }

    // ---------------------------------------------------------------
    // updateAvatar
    // ---------------------------------------------------------------

    @Nested
    class UpdateAvatar {

        @Test
        void throwsIllegalArgumentException_whenAvatarPathIsNotAllowed() {
            assertThatThrownBy(() -> userService.updateAvatar(TestFixtures.USER_ID, "/avatars/not-allowed.png"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Avatar path does not exist");

            verifyNoInteractions(userRepository);
        }

        @Test
        void throwsEntityNotFoundException_whenUserDoesNotExist() {
            String avatarPath = AvatarConstants.ALLOWED_AVATARS.iterator().next();
            when(userRepository.findById(TestFixtures.USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateAvatar(TestFixtures.USER_ID, avatarPath))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("User not found");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void updatesAvatarAndReturnsProfileResponse_whenUserExists() {
            String avatarPath = "/avatars/avatar1.png";
            User user = TestFixtures.aUser();

            when(userRepository.findById(TestFixtures.USER_ID)).thenReturn(Optional.of(user));
            when(ratingRepository.findAverageRatingByAuthorId(TestFixtures.USER_ID)).thenReturn(4.5);
            when(userRepository.countMoviesWatched(TestFixtures.USER_ID)).thenReturn(3);
            when(userRepository.countMoviesToWatch(TestFixtures.USER_ID)).thenReturn(2);
            when(ratingRepository.countByAuthorId(TestFixtures.USER_ID)).thenReturn(5);
            when(reviewRepository.countByAuthorId(TestFixtures.USER_ID)).thenReturn(1);

            UserProfileResponse result = userService.updateAvatar(TestFixtures.USER_ID, avatarPath);

            assertThat(user.getAvatarPath()).isEqualTo(avatarPath);
            verify(userRepository).save(user);

            assertThat(result.username()).isEqualTo(TestFixtures.USERNAME);
            assertThat(result.avatarPath()).isEqualTo(avatarPath);
            assertThat(result.avgRating()).isEqualTo(4.5);
            assertThat(result.moviesWatchedCount()).isEqualTo(3);
            assertThat(result.moviesToWatchCount()).isEqualTo(2);
            assertThat(result.ratingsCount()).isEqualTo(5);
            assertThat(result.reviewsCount()).isEqualTo(1);
        }

        @Test
        void defaultsAvgRatingToZero_whenNoRatingsExist() {
            String avatarPath = "/avatars/avatar1.png";
            User user = TestFixtures.aUser();

            when(userRepository.findById(TestFixtures.USER_ID)).thenReturn(Optional.of(user));
            when(ratingRepository.findAverageRatingByAuthorId(TestFixtures.USER_ID)).thenReturn(null);
            when(userRepository.countMoviesWatched(TestFixtures.USER_ID)).thenReturn(0);
            when(userRepository.countMoviesToWatch(TestFixtures.USER_ID)).thenReturn(0);
            when(ratingRepository.countByAuthorId(TestFixtures.USER_ID)).thenReturn(0);
            when(reviewRepository.countByAuthorId(TestFixtures.USER_ID)).thenReturn(0);

            UserProfileResponse result = userService.updateAvatar(TestFixtures.USER_ID, avatarPath);

            assertThat(result.avgRating()).isEqualTo(0.0);
        }
    }

    // ---------------------------------------------------------------
    // getUserProfile
    // ---------------------------------------------------------------

    @Nested
    class GetUserProfile {

        @Test
        void returnsProfileResponse_whenUserExists() {
            User user = TestFixtures.aUser();

            when(userRepository.findByUsername(TestFixtures.USERNAME)).thenReturn(Optional.of(user));
            when(ratingRepository.findAverageRatingByAuthorId(TestFixtures.USER_ID)).thenReturn(3.0);
            when(userRepository.countMoviesWatched(TestFixtures.USER_ID)).thenReturn(7);
            when(userRepository.countMoviesToWatch(TestFixtures.USER_ID)).thenReturn(4);
            when(ratingRepository.countByAuthorId(TestFixtures.USER_ID)).thenReturn(6);
            when(reviewRepository.countByAuthorId(TestFixtures.USER_ID)).thenReturn(2);

            UserProfileResponse result = userService.getUserProfile(TestFixtures.USERNAME);

            assertThat(result.username()).isEqualTo(TestFixtures.USERNAME);
            assertThat(result.avgRating()).isEqualTo(3.0);
            assertThat(result.moviesWatchedCount()).isEqualTo(7);
            assertThat(result.moviesToWatchCount()).isEqualTo(4);
            assertThat(result.ratingsCount()).isEqualTo(6);
            assertThat(result.reviewsCount()).isEqualTo(2);
        }

        @Test
        void throwsEntityNotFoundException_whenUserDoesNotExist() {
            when(userRepository.findByUsername(TestFixtures.USERNAME)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserProfile(TestFixtures.USERNAME))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("User not found");

            verifyNoInteractions(ratingRepository, reviewRepository);
        }
    }

    // ---------------------------------------------------------------
    // getUserReviews
    // ---------------------------------------------------------------

    @Nested
    class GetUserReviews {

        private final Pageable pageable = PageRequest.of(0, 10);

        @Test
        void returnsMappedReviews_whenSortIsNewest() {
            User user = TestFixtures.aUser();
            Review review = mock(Review.class);
            ProfileReviewDto dto = mock(ProfileReviewDto.class);
            Page<Review> page = new PageImpl<>(List.of(review));

            when(userRepository.findByUsername(TestFixtures.USERNAME)).thenReturn(Optional.of(user));
            when(reviewRepository.findByAuthorId(eq(TestFixtures.USER_ID), any(Pageable.class))).thenReturn(page);
            when(reviewMapper.toProfileReviewDto(review)).thenReturn(dto);

            Page<ProfileReviewDto> result = userService.getUserReviews(TestFixtures.USERNAME, pageable, "newest");

            assertThat(result.getContent()).containsExactly(dto);

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(reviewRepository).findByAuthorId(eq(TestFixtures.USER_ID), captor.capture());
            assertThat(captor.getValue().getSort().getOrderFor("createdAt").getDirection())
                    .isEqualTo(Sort.Direction.DESC);
        }

        @Test
        void sortsAscending_whenSortIsOldest() {
            User user = TestFixtures.aUser();
            when(userRepository.findByUsername(TestFixtures.USERNAME)).thenReturn(Optional.of(user));
            when(reviewRepository.findByAuthorId(eq(TestFixtures.USER_ID), any(Pageable.class))).thenReturn(Page.empty());

            userService.getUserReviews(TestFixtures.USERNAME, pageable, "oldest");

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(reviewRepository).findByAuthorId(eq(TestFixtures.USER_ID), captor.capture());
            assertThat(captor.getValue().getSort().getOrderFor("createdAt").getDirection())
                    .isEqualTo(Sort.Direction.ASC);
        }

        @Test
        void throwsIllegalArgumentException_forInvalidSort() {
            User user = TestFixtures.aUser();
            when(userRepository.findByUsername(TestFixtures.USERNAME)).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> userService.getUserReviews(TestFixtures.USERNAME, pageable, "random"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid sort parameter: random");

            verifyNoInteractions(reviewRepository);
        }

        @Test
        void throwsEntityNotFoundException_whenUserDoesNotExist() {
            when(userRepository.findByUsername(TestFixtures.USERNAME)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserReviews(TestFixtures.USERNAME, pageable, "newest"))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("User not found");

            verifyNoInteractions(reviewRepository);
        }
    }

    // ---------------------------------------------------------------
    // getUserRatings
    // ---------------------------------------------------------------

    @Nested
    class GetUserRatings {

        private final Pageable pageable = PageRequest.of(0, 10);

        @Test
        void returnsMappedRatings_whenSortIsValid() {
            User user = TestFixtures.aUser();
            Rating rating = mock(Rating.class);
            ProfileRatingDto dto = mock(ProfileRatingDto.class);
            Page<Rating> page = new PageImpl<>(List.of(rating));

            when(userRepository.findByUsername(TestFixtures.USERNAME)).thenReturn(Optional.of(user));
            when(ratingRepository.findByAuthorId(eq(TestFixtures.USER_ID), any(Pageable.class))).thenReturn(page);
            when(ratingMapper.toProfileRatingDto(rating)).thenReturn(dto);

            Page<ProfileRatingDto> result = userService.getUserRatings(TestFixtures.USERNAME, pageable, "highest");

            assertThat(result.getContent()).containsExactly(dto);
        }

        @ParameterizedTest
        @CsvSource({
                "newest, createdAt, DESC",
                "oldest, createdAt, ASC",
                "highest, rating, DESC",
                "lowest, rating, ASC"
        })
        void appliesExpectedSortForEachOption(String sortParam, String property, Sort.Direction direction) {
            User user = TestFixtures.aUser();
            when(userRepository.findByUsername(TestFixtures.USERNAME)).thenReturn(Optional.of(user));
            when(ratingRepository.findByAuthorId(eq(TestFixtures.USER_ID), any(Pageable.class))).thenReturn(Page.empty());

            userService.getUserRatings(TestFixtures.USERNAME, pageable, sortParam);

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(ratingRepository).findByAuthorId(eq(TestFixtures.USER_ID), captor.capture());
            assertThat(captor.getValue().getSort().getOrderFor(property).getDirection()).isEqualTo(direction);
        }

        @Test
        void throwsIllegalArgumentException_forInvalidSort() {
            User user = TestFixtures.aUser();
            when(userRepository.findByUsername(TestFixtures.USERNAME)).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> userService.getUserRatings(TestFixtures.USERNAME, pageable, "random"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid sort parameter: random");

            verifyNoInteractions(ratingRepository);
        }

        @Test
        void throwsEntityNotFoundException_whenUserDoesNotExist() {
            when(userRepository.findByUsername(TestFixtures.USERNAME)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserRatings(TestFixtures.USERNAME, pageable, "newest"))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("User not found");

            verifyNoInteractions(ratingRepository);
        }
    }
}