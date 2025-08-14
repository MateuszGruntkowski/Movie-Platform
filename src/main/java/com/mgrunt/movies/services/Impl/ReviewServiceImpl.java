package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.documents.Movie;
import com.mgrunt.movies.domain.documents.Review;
import com.mgrunt.movies.domain.documents.User;
import com.mgrunt.movies.domain.dtos.ReviewDto;
import com.mgrunt.movies.mappers.ReviewMapper;
import com.mgrunt.movies.repositories.ReviewRepository;
import com.mgrunt.movies.repositories.UserRepository;
import com.mgrunt.movies.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ReviewDto createReview(String imdbId, String reviewBody, Principal principal) {

        if (imdbId == null || imdbId.isEmpty()) {
            throw new IllegalArgumentException("IMDB ID cannot be null or empty");
        }

        if (reviewBody == null || reviewBody.isEmpty()) {
            throw new IllegalArgumentException("Review body cannot be null or empty");
        }

        User currentUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = reviewRepository.insert(new Review(reviewBody, currentUser.getId()));

        mongoTemplate.update(Movie.class)
                .matching(Criteria.where("imdbId").is(imdbId))
                .apply(new Update().push("reviewIds").value(review.getId()))
                .first();

        mongoTemplate.update(User.class)
                .matching(Criteria.where("username").is(currentUser.getUsername()))
                .apply(new Update().push("reviewIds").value(review.getId()))
                .first();

        ReviewDto reviewDto = reviewMapper.toDto(review);
        reviewDto.setAuthorUsername(currentUser.getUsername());

        return reviewDto;
    }
}
