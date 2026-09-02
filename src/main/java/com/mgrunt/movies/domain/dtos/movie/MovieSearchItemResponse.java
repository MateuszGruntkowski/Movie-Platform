package com.mgrunt.movies.domain.dtos.movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieSearchItemResponse(
        Long id,
        String title,
        String overview,
        String releaseDate,
        Double voteAverage,
        Integer voteCount,
        Double popularity,
        String posterPath,
        String backdropPath
) {
}