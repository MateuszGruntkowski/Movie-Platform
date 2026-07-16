package com.mgrunt.movies.domain.dtos.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbImageWrapperResponse {
    private Long id;
    private List<TmdbImageResponse> backdrops;
}
