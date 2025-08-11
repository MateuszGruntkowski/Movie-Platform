package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.documents.Review;

public interface ReviewService {
    Review createReview(String imdbId, String reviewBody);
}
