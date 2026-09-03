package com.mgrunt.movies.domain.dtos.movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TrendingMovieResponse(
        Long tmdbId,
        String title,
        String posterPath,
        String backdropPath
) {
}