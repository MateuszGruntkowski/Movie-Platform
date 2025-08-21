package com.mgrunt.movies.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class TmdbMovieDetailsResponse {
    private Long id;

    @JsonProperty("imdb_id")
    private String imdbId;

    private String title;
    private String overview;

    @JsonProperty("release_date")
    private LocalDate releaseDate;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("vote_average")
    private Double voteAverage;

    @JsonProperty("vote_count")
    private Integer voteCount;

    private Double popularity;
    private Integer runtime;

    private List<TmdbGenreResponse> genres;

    @JsonProperty("belongs_to_collection")
    private TmdbCollectionResponse belongsToCollection;
}
