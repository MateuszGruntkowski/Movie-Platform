package com.mgrunt.movies.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDataDto {
    private String imdbId;
    private String title;
    private LocalDate releaseDate;
    private String trailerUrl;
    private List<String> genres;
    private String poster;
    private List<String> backdrops;

    @JsonIgnore
    private List<String> reviewIds;
}
