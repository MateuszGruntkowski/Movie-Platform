package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.profile.ProfileRatingDto;
import com.mgrunt.movies.domain.dtos.profile.ProfileReviewDto;
import com.mgrunt.movies.domain.dtos.rating.RatingDto;
import com.mgrunt.movies.domain.dtos.review.ReviewDto;
import com.mgrunt.movies.domain.dtos.user.UserDto;
import com.mgrunt.movies.domain.dtos.profile.UserProfileResponse;
import com.mgrunt.movies.domain.entities.User;
import com.mgrunt.movies.mappers.RatingMapper;
import com.mgrunt.movies.mappers.ReviewMapper;
import com.mgrunt.movies.mappers.UserMapper;
import com.mgrunt.movies.repositories.RatingRepository;
import com.mgrunt.movies.repositories.ReviewRepository;
import com.mgrunt.movies.repositories.UserRepository;
import com.mgrunt.movies.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;

    private static final Set<String> ALLOWED_AVATARS = Set.of(
            "/avatars/avatar1.png",
            "/avatars/avatar2.png"
    );

    @Transactional
    @Override
    public UserDto getUser(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsernameWithDetails(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return buildUserProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateAvatar(UUID id, String avatarPath) {
        if (!ALLOWED_AVATARS.contains(avatarPath)) {
            throw new IllegalArgumentException("Avatar path does not exist");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setAvatarPath(avatarPath);
        userRepository.save(user);

        return buildUserProfileResponse(user);
    }

    private UserProfileResponse buildUserProfileResponse(User user) {
        UUID id = user.getId();

        List<ProfileReviewDto> reviews = reviewRepository.findByAuthorId(id).stream()
                .map(reviewMapper::toProfileReviewDto)
                .toList();

        List<ProfileRatingDto> ratings = ratingRepository.findByAuthorId(id).stream()
                .map(ratingMapper::toProfileRatingDto)
                .toList();

        double avgRating = Optional.ofNullable(ratingRepository.findAverageRatingByAuthorId(id)).orElse(0.0);
        int moviesWatchedCount = userRepository.countMoviesWatched(id);
        int moviesToWatchCount = userRepository.countMoviesToWatch(id);

        return UserProfileResponse.builder()
                .username(user.getUsername())
                .avatarPath(user.getAvatarPath())
                .avgRating(avgRating)
                .ratingsCount(ratings.size())
                .reviewsCount(reviews.size())
                .moviesWatchedCount(moviesWatchedCount)
                .moviesToWatchCount(moviesToWatchCount)
                .reviews(reviews)
                .ratings(ratings)
                .build();
    }
}
