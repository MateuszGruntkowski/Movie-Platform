package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.Security.CustomUserDetails;
import com.mgrunt.movies.domain.dtos.rating.RatingResponse;
import com.mgrunt.movies.domain.entities.Rating;
import com.mgrunt.movies.mappers.RatingMapper;
import com.mgrunt.movies.repositories.RatingRepository;
import com.mgrunt.movies.services.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;

    @Override
    public Optional<RatingResponse> getUserRatingForMovie(Long tmdbId, Authentication auth) {

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        UUID userId = userDetails.getId();

        return ratingRepository.findByTmdbIdAndAuthorId(tmdbId, userId)
                .map(ratingMapper::toRatingResponse);
    }
}
