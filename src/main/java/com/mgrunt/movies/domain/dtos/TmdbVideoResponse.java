package com.mgrunt.movies.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbVideoResponse {
    private String id;
    private String key;
    private String name;
    private String site;
    private String type;
    private Boolean official;
    private String size;

    @JsonProperty("published_at")
    private String publishedAt;
}
