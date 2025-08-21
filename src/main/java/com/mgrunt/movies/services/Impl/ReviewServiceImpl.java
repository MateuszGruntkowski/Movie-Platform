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
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;
    private final MovieMapper movieMapper;

//    @Autowired
//    private MongoTemplate mongoTemplate;

    @Override
    public ReviewDto createReview(String imdbId, String reviewBody, Authentication authentication) {

        if (reviewBody == null || reviewBody.trim().isEmpty()) {
            throw new IllegalArgumentException("Review body cannot be empty");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Movie movie = movieRepository.findByImdbId(imdbId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));


//        if (reviewRepository.existsByAuthorIdAndMovieId(userId, movie.getId())) {
//            throw new RuntimeException("User already reviewed this movie");
//        }

        Review review = Review.builder()
                .body(reviewBody)
                .author(currentUser)
                .movie(movie)
                .build();
        Review savedReview = reviewRepository.save(review);

        return reviewMapper.toDto(savedReview);
    }

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
    public ReviewDto createReview(ReviewRequest reviewRequest, Authentication authentication) {

        String reviewBody = reviewRequest.getReviewBody();
        String imdbId = reviewRequest.getMovie().getImdbId();

        if (reviewBody == null || reviewBody.trim().isEmpty()) {
            throw new IllegalArgumentException("Review body cannot be empty");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Optional<Movie> movieOpt = movieRepository.findByImdbId(imdbId);
        Movie movie;
        if(movieOpt.isEmpty()){
            movie = movieMapper.toEntity(reviewRequest.getMovie());
            movie.setReviews(new ArrayList<>());
            movie = movieRepository.save(movie);
        }else{
            movie = movieOpt.get();
        }

        Review review = Review.builder()
                .body(reviewBody)
                .author(currentUser)
                .movie(movie)
                .build();

        reviewRepository.save(review);

        movie.getReviews().add(review);
        movieRepository.save(movie);

        return reviewMapper.toDto(review);
    }

}
