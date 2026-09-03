package com.mgrunt.movies.domain.dtos.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbGenreResponse(
        Long id,
        String name
) {
}