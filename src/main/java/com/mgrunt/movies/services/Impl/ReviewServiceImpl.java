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
import com.mgrunt.movies.services.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;
    private final MovieService movieService;

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDto> getReviewsForMovie(Long tmdbId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByMovieTmdbId(tmdbId, pageable);

        return reviews.map(reviewMapper::toDto);
    }

    @Override
    @Transactional
    public ReviewDto createReview(Long tmdbId, ReviewRequest reviewRequest, UUID authorId) {
        String reviewBody = reviewRequest.reviewBody();

        if(reviewBody == null || reviewBody.trim().isEmpty()){
            throw new IllegalArgumentException("Review body cannot be null");
        }

        User currentUser = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Movie movie = movieService.getMovie(tmdbId);

        Review review = Review.builder()
                .movie(movie)
                .author(currentUser)
                .body(reviewBody)
                .build();
        reviewRepository.save(review);

        movie.getReviews().add(review);
        return reviewMapper.toDto(review);
    }

    @Override
    @Transactional
    public void deleteReview(UUID reviewId, UUID authorId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        if(!review.getAuthor().getId().equals(authorId)){
            throw new SecurityException("You are not authorized to delete this review");
        }
        reviewRepository.delete(review);
    }

}
