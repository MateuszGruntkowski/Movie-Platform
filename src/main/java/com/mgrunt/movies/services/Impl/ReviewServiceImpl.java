package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.Security.CustomUserDetails;
import com.mgrunt.movies.domain.dtos.ReviewDto;
import com.mgrunt.movies.domain.dtos.ReviewRequest;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.domain.entities.Review;
import com.mgrunt.movies.domain.entities.User;
import com.mgrunt.movies.mappers.MovieMapper;
import com.mgrunt.movies.mappers.ReviewMapper;
import com.mgrunt.movies.repositories.MovieRepository;
import com.mgrunt.movies.repositories.ReviewRepository;
import com.mgrunt.movies.repositories.UserRepository;
import com.mgrunt.movies.services.MovieService;
import com.mgrunt.movies.services.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;
    private final MovieService movieService;

    @Override
    public List<ReviewDto> getReviewsForMovie(Long tmdbId) {
//        Movie movie = movieRepository.findById(movieId)
//                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        List<Review> reviews = reviewRepository.findByMovieTmdbId(Long.valueOf(tmdbId));

        return reviews.stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @Override
    public ReviewDto createReview(Long tmdbId, ReviewRequest reviewRequest, Authentication authentication) {
        String reviewBody = reviewRequest.getReviewBody();

        if(reviewBody == null || reviewBody.trim().isEmpty()){
            throw new IllegalArgumentException("Review body cannot be null");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Movie movie = movieService.findOrCreateMovie(tmdbId);

        Review review = Review.builder()
                .movie(movie)
                .author(currentUser)
                .body(reviewBody)
                .build();
        reviewRepository.save(review);

        movie.getReviews().add(review);
        return reviewMapper.toDto(review);

    }

}
