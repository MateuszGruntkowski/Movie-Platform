package com.mgrunt.movies.domain.dtos.movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieSearchResponse(
        List<MovieSearchItemResponse> results,
        Integer page,
        Integer totalPages,
        Integer totalResults
) {
}