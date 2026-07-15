package com.mgrunt.movies.mappers.support;
import org.springframework.stereotype.Component;
import org.mapstruct.Named;

@Component
public class TmdbUrlBuilder {

    private static final String BASE_URL = "https://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w500";
    private static final String BACKDROP_SIZE = "original";

    @Named("buildFullPosterUrl")
    public String buildFullPosterUrl(String path) {
        return buildUrl(path, POSTER_SIZE);
    }

    @Named("buildFullBackdropUrl")
    public String buildFullBackdropUrl(String path) {
        return buildUrl(path, BACKDROP_SIZE);
    }

    private String buildUrl(String path, String size) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        return BASE_URL + size + path;
    }
}