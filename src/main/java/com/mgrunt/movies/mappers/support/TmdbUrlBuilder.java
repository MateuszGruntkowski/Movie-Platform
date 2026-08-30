package com.mgrunt.movies.mappers.support;
import org.springframework.stereotype.Component;
import org.mapstruct.Named;

@Component
public class TmdbUrlBuilder {

    private static final String BASE_URL = "https://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w500";
    private static final String NORMAL_BACKDROP_SIZE = "w1280";
    private static final String TRENDING_MOVIE_BACKDROP_SIZE = "original";

    @Named("buildPosterUrl")
    public String buildPosterUrl(String path) {
        return buildUrl(path, POSTER_SIZE);
    }

    @Named("buildBackdropUrl")
    public String buildBackdropUrl(String path) {
        return buildUrl(path, NORMAL_BACKDROP_SIZE);
    }

    @Named("buildFullTrendingMovieBackdropUrl")
    public String buildTrendingMovieBackdropUrl(String path) {
        return buildUrl(path, TRENDING_MOVIE_BACKDROP_SIZE);
    }

    private String buildUrl(String path, String size) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        return BASE_URL + size + path;
    }
}