package com.mgrunt.movies.domain.dtos.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbTrendingMovieItemResponse {
    private Long id;
    private String title;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

}
