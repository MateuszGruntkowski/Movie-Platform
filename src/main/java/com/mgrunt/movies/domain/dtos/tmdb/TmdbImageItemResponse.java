package com.mgrunt.movies.domain.dtos.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbImageItemResponse(

        @JsonProperty("aspect_ratio")
        Double aspectRatio,

        Integer height,
        Integer width,

        @JsonProperty("file_path")
        String filePath,

        @JsonProperty("vote_average")
        Double voteAverage,

        @JsonProperty("vote_count")
        Integer voteCount
) {
}