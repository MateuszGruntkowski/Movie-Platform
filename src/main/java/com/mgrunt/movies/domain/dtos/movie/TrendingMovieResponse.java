package com.mgrunt.movies.domain.dtos.movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrendingMovieResponse{
    private Long tmdbId;
    private String title;
    private String posterPath;
    private String backdropPath;
}
