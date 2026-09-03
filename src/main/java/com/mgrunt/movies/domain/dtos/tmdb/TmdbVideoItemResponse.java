package com.mgrunt.movies.domain.dtos.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbVideoItemResponse(
        String id,
        String key,
        String name,
        String site,
        String type,
        Boolean official,
        String size,

        @JsonProperty("published_at")
        String publishedAt
) {
}