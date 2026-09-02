package com.mgrunt.movies.domain.dtos.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbMovieDetailsResponse(
        Long id,

        @JsonProperty("imdb_id")
        String imdbId,

        String title,
        String overview,

        @JsonProperty("release_date")
        LocalDate releaseDate,

        @JsonProperty("poster_path")
        String posterPath,

        @JsonProperty("backdrop_path")
        String backdropPath,

        @JsonProperty("vote_average")
        Double voteAverage,

        @JsonProperty("vote_count")
        Integer voteCount,

        Double popularity,
        Integer runtime,

        List<TmdbGenreResponse> genres,

        @JsonProperty("original_language")
        String originalLanguage,

        Boolean adult,
        Integer budget,
        Integer revenue,
        String tagline
) {
}