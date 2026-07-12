package com.mgrunt.movies.mappers;

import com.mgrunt.movies.domain.dtos.*;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.domain.entities.Review;
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

    @Mapping(target = "backdrops", source = "backdrops")
    @Mapping(target = "reviews", source = "reviews")
    @Mapping(target = "genres", source = "genres", qualifiedByName = "mapGenres")
    @Mapping(target = "belongsToCollection", ignore = true)
    @Mapping(target = "posterPath", source = "posterPath", qualifiedByName = "buildFullUrl")
    @Mapping(target = "backdropPath", source = "backdropPath", qualifiedByName = "buildFullUrl")
    MovieDetailsResponse toMovieDetailsResponse(Movie movie);

    @Mapping(target = "posterUrl", source = "posterPath", qualifiedByName = "buildFullUrl")
    @Mapping(target = "backdropUrl", source = "backdropPath", qualifiedByName = "buildFullUrl")
    MovieSearchResponse toMovieSearchResponse(TmdbMovieSearchResult searchResult);

    List<MovieSearchResponse> toMovieSearchResponseList(List<TmdbMovieSearchResult> searchResults);

    @Mapping(target = "results", source = "results")
    @Mapping(target = "page", source = "page")
    @Mapping(target = "totalPages", source = "totalPages")
    @Mapping(target = "totalResults", source = "totalResults")
    MovieSearchPageResponse toMovieSearchPageResponse(TmdbSearchResponse tmdbSearchResponse);

    @Named("buildFullUrl")
    default String buildFullUrl(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        return "https://image.tmdb.org/t/p/w500" + path;
    }

    @Named("mapGenres")
    default List<TmdbGenreResponse> mapGenres(List<String> genres) {
        if (genres == null) return new ArrayList<>();
        return genres.stream()
                .map(name -> TmdbGenreResponse.builder().name(name).build())
                .toList();
    }
}