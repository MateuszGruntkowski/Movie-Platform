package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.Security.CustomUserDetails;
import com.mgrunt.movies.domain.dtos.rating.RatingRequest;
import com.mgrunt.movies.domain.dtos.rating.RatingResponse;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.domain.entities.Rating;
import com.mgrunt.movies.domain.entities.User;
import com.mgrunt.movies.mappers.RatingMapper;
import com.mgrunt.movies.repositories.RatingRepository;
import com.mgrunt.movies.repositories.UserRepository;
import com.mgrunt.movies.services.MovieService;
import com.mgrunt.movies.services.RatingService;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final MovieService movieService;
    private final UserRepository userRepository;

    @Override
    public Optional<RatingResponse> getUserRatingForMovie(Long tmdbId, UUID authorId) {
        return ratingRepository.findByMovie_TmdbIdAndAuthorId(tmdbId, authorId)
                .map(ratingMapper::toRatingResponse);
    }

    @Override
    @Transactional
    public RatingResponse rateMovie(Long tmdbId, UUID authorId, Integer ratingValue) {

        Optional<Rating> existingRatingOpt = ratingRepository.findByMovie_TmdbIdAndAuthorId(tmdbId, authorId);

        Rating rating;

        if (existingRatingOpt.isPresent()) {
            rating = existingRatingOpt.get();
            rating.setRating(ratingValue);
        } else {
            Movie movie = movieService.getMovie(tmdbId);
            User author = userRepository.getReferenceById(authorId);

            rating = Rating.builder()
                    .rating(ratingValue)
                    .movie(movie)
                    .author(author)
                    .build();
        }

        Rating saved = ratingRepository.save(rating);
        return ratingMapper.toRatingResponse(saved);
    }
}
