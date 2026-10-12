package com.mgrunt.movies.domain.dtos.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbTrendingMovieItemResponse(
        Long id,
        String title,
        String overview,

        @JsonProperty("release_date")
        LocalDate releaseDate,

        @JsonProperty("poster_path")
        String posterPath,

        @JsonProperty("backdrop_path")
        String backdropPath,

        @JsonProperty("vote_average")
        Double voteAverage
) {
}