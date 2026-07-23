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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

//    @Override
//    @Transactional(readOnly = true)
//    public UserProfileResponse getUserProfile(UUID id) {
//        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
//        return buildUserProfileResponse(user);
//    }


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

    @Override
    public UserProfileResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return buildUserProfileResponse(user);
    }

    @Override
    public Page<ProfileReviewDto> getUserReviews(String username, Pageable pageable, String sort) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Sort sortOrder = switch (sort) {
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
            default -> throw new IllegalArgumentException("Invalid sort parameter: " + sort);
        };

        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);

        return reviewRepository.findByAuthorId(user.getId(), pageableWithSort)
                .map(reviewMapper::toProfileReviewDto);
    }

    @Override
    public Page<ProfileRatingDto> getUserRatings(String username, Pageable pageable, String sort) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found"));

        Sort sortOrder = switch (sort) {
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "highest" -> Sort.by(Sort.Direction.DESC, "rating");
            case "lowest" -> Sort.by(Sort.Direction.ASC, "rating");
            default -> throw new IllegalArgumentException("Invalid sort parameter: " + sort);
        };

        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);

        return ratingRepository.findByAuthorId(user.getId(), pageableWithSort)
                .map(ratingMapper::toProfileRatingDto);

    }

    private UserProfileResponse buildUserProfileResponse(User user) {
        UUID id = user.getId();

        double avgRating = Optional.ofNullable(ratingRepository.findAverageRatingByAuthorId(id)).orElse(0.0);
        int moviesWatchedCount = userRepository.countMoviesWatched(id);
        int moviesToWatchCount = userRepository.countMoviesToWatch(id);
        int ratingsCount = ratingRepository.countByAuthorId(id);
        int reviewsCount = reviewRepository.countByAuthorId(id);

        return UserProfileResponse.builder()
                .username(user.getUsername())
                .avatarPath(user.getAvatarPath())
                .avgRating(avgRating)
                .ratingsCount(ratingsCount)
                .reviewsCount(reviewsCount)
                .moviesWatchedCount(moviesWatchedCount)
                .moviesToWatchCount(moviesToWatchCount)
                .build();
    }
}
