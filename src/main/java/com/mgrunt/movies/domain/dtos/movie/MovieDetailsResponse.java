package com.mgrunt.movies.domain.dtos.movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbGenreResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDetailsResponse {
    private Long tmdbId;
    private String imdbId;
    private String title;
    private String overview;
    private LocalDate releaseDate;
    private String posterPath;
    private String backdropPath;
    private Double voteAverage;
    private Integer voteCount;
    private Double popularity;
    private Integer runtime;
    private List<TmdbGenreResponse> genres;
    private String originalLanguage;
    private Boolean adult;
    private Integer budget;
    private Integer revenue;
    private String tagline;
    private String trailerUrl;
    private List<String> backdrops;
}
