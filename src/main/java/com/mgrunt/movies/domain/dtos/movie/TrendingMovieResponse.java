package com.mgrunt.movies.domain.dtos.movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TrendingMovieResponse(
        Long tmdbId,
        String title,
        String overview,
        LocalDate releaseDate,
        String posterPath,
        String backdropPath,
        Double voteAverage
) {
}