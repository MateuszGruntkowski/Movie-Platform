package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.Security.CustomUserDetails;
import com.mgrunt.movies.domain.dtos.ReviewDto;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.domain.entities.Review;
import com.mgrunt.movies.domain.entities.User;
import com.mgrunt.movies.mappers.ReviewMapper;
import com.mgrunt.movies.repositories.MovieRepository;
import com.mgrunt.movies.repositories.ReviewRepository;
import com.mgrunt.movies.repositories.UserRepository;
import com.mgrunt.movies.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

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
                .orElseThrow(() -> new RuntimeException("User not found"));

        Movie movie = movieRepository.findByImdbId(imdbId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        if (reviewRepository.existsByAuthorIdAndMovieId(userId, movie.getId())) {
            throw new RuntimeException("User already reviewed this movie");
        }

        Review review = Review.builder()
                .body(reviewBody)
                .author(currentUser)
                .movie(movie)
                .build();
        Review savedReview = reviewRepository.save(review);

//        Review review = reviewRepository1.insert(new Review(reviewBody, currentUser.getId()));

//        mongoTemplate.update(Movie.class)
//                .matching(Criteria.where("imdbId").is(imdbId))
//                .apply(new Update().push("reviewIds").value(review.getId()))
//                .first();
//
//        mongoTemplate.update(User.class)
//                .matching(Criteria.where("username").is(currentUser.getUsername()))
//                .apply(new Update().push("reviewIds").value(review.getId()))
//                .first();

        return reviewMapper.toDto(savedReview);
    }
}
