package com.mgrunt.movies.mappers;


import com.mgrunt.movies.domain.dtos.*;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.domain.entities.Review;
import com.mgrunt.movies.services.TmdbService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ReviewMapper.class}
)
public interface MovieDetailsMapper {

    // MAPPER FOR TMDB -> MovieDetailsResponse
    @Mapping(target = "tmdbId", source = "tmdbData.id")
    @Mapping(target = "imdbId", source = "tmdbData.imdbId")
    @Mapping(target = "title", source = "tmdbData.title")
    @Mapping(target = "overview", source = "tmdbData.overview")
    @Mapping(target = "releaseDate", source = "tmdbData.releaseDate")
    @Mapping(target = "posterPath", source = "tmdbData.posterPath", qualifiedByName = "buildFullUrl")
    @Mapping(target = "backdropPath", source = "tmdbData.backdropPath", qualifiedByName = "buildFullUrl")
    @Mapping(target = "voteAverage", source = "tmdbData.voteAverage")
    @Mapping(target = "voteCount", source = "tmdbData.voteCount")
    @Mapping(target = "popularity", source = "tmdbData.popularity")
    @Mapping(target = "runtime", source = "tmdbData.runtime")
    @Mapping(target = "genres", source = "tmdbData.genres")
    @Mapping(target = "belongsToCollection", source = "tmdbData.belongsToCollection")
    @Mapping(target = "trailerUrl", source = "trailerUrl")
    @Mapping(target = "backdrops", source = "backdrops")
    @Mapping(target = "reviews", source = "reviews")
    MovieDetailsResponse toMovieDetailsResponse(
            TmdbMovieDetailsResponse tmdbData,
            String trailerUrl,
            List<String> backdrops,
            List<Review> reviews
    );

    @Mapping(target = "posterUrl", expression = "java(buildFullUrl(searchResult.getPosterPath()))")
    @Mapping(target = "backdropPath", expression = "java(buildFullUrl(searchResult.getBackdropPath()))")
    MovieSearchResponse toMovieSearchResponse(TmdbMovieSearchResult searchResult);

    @Mapping(target = "posterUrl", source = "posterPath", qualifiedByName = "buildFullUrl")
    @Mapping(target = "posterPath", source = "posterPath") // oryginalny path
    @Mapping(target = "backdropPath", source = "backdropPath", qualifiedByName = "buildFullUrl")
    List<MovieSearchResponse> toMovieSearchResponseList(List<TmdbMovieSearchResult> searchResults);

    // Helper method for URL and images
    @Named("buildFullUrl")
    default String buildFullUrl(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        return "https://image.tmdb.org/t/p/w500" + path;
    }

    // Mapper for Movie -> MovieDetailsResponse

    @Mapping(target = "backdrops", source = "backdrops")
    @Mapping(target = "reviews", source = "reviews")
    @Mapping(target = "genres", source = "genres", qualifiedByName = "mapGenres")
    @Mapping(target = "belongsToCollection", ignore = true)
    @Mapping(target = "posterPath", source = "posterPath", qualifiedByName = "buildFullUrl")
    @Mapping(target = "backdropPath", source = "backdropPath", qualifiedByName = "buildFullUrl")
    MovieDetailsResponse toMovieDetailsResponse(Movie movie);

    @Named("mapGenres")
    default List<TmdbGenreResponse> mapGenres(List<String> genres) {
        if (genres == null) return new ArrayList<>();
        return genres.stream()
                .map(name -> {
                    TmdbGenreResponse g = new TmdbGenreResponse();
                    g.setName(name);
                    return g;
                })
                .toList();
    }
}
