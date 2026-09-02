package com.mgrunt.movies.domain.dtos.movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbGenreResponse;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieDetailsResponse(
        Long tmdbId,
        String imdbId,
        String title,
        String overview,
        LocalDate releaseDate,
        String posterPath,
        String backdropPath,
        Double voteAverage,
        Integer voteCount,
        Double popularity,
        Integer runtime,
        List<TmdbGenreResponse> genres,
        String originalLanguage,
        Boolean adult,
        Integer budget,
        Integer revenue,
        String tagline,
        String trailerUrl,
        List<String> backdrops
) {
}