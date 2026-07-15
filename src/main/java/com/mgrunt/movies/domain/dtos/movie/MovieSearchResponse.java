package com.mgrunt.movies.domain.dtos.movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieSearchResponse {
    private Long id;
    private String title;
    private String overview;
    private String releaseDate;
    private Double voteAverage;
    private Integer voteCount;
    private Double popularity;
    private String posterPath;
    private String backdropPath;
}