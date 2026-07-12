package com.mgrunt.movies.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * The data structure of the seed file (src/main/resources/data/movies.json),
 * used exclusively by MovieDataLoader to pre-populate the database.
 * Not to be confused with API response DTOs (MovieDetailsResponse, MovieSearchResponse).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieSeedDto {
    private String imdbId;
    private Long tmdbId;
    private String title;
    private LocalDate releaseDate;
    private String trailerUrl;
    private List<String> genres;
    private String poster;
    private List<String> backdrops;

    @JsonIgnore
    private List<String> reviewIds;
}