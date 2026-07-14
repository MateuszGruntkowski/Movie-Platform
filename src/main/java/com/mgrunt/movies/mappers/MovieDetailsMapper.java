package com.mgrunt.movies.mappers;

import com.mgrunt.movies.domain.dtos.movie.MovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.movie.MovieSearchPageResponse;
import com.mgrunt.movies.domain.dtos.movie.MovieSearchResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbGenreResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbMovieSearchResult;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbSearchResponse;
import com.mgrunt.movies.domain.entities.Movie;
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

    @Mapping(target = "backdrops", source = "backdrops", qualifiedByName = "buildFullBackdropUrl")
    @Mapping(target = "genres", source = "genres", qualifiedByName = "mapGenres")
    @Mapping(target = "belongsToCollection", ignore = true)
    @Mapping(target = "posterPath", source = "posterPath", qualifiedByName = "buildFullPosterUrl")
    @Mapping(target = "backdropPath", source = "backdropPath", qualifiedByName = "buildFullBackdropUrl")
    MovieDetailsResponse toMovieDetailsResponse(Movie movie);

    @Mapping(target = "posterUrl", source = "posterPath", qualifiedByName = "buildFullPosterUrl")
    @Mapping(target = "backdropUrl", source = "backdropPath", qualifiedByName = "buildFullBackdropUrl")
    MovieSearchResponse toMovieSearchResponse(TmdbMovieSearchResult searchResult);

    @Mapping(target = "results", source = "results")
    @Mapping(target = "page", source = "page")
    @Mapping(target = "totalPages", source = "totalPages")
    @Mapping(target = "totalResults", source = "totalResults")
    MovieSearchPageResponse toMovieSearchPageResponse(TmdbSearchResponse tmdbSearchResponse);

    @Named("buildFullPosterUrl")
    default String buildFullPosterUrl(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        return "https://image.tmdb.org/t/p/w500" + path;
    }

    @Named("buildFullBackdropUrl")
    default String buildFullBackdropUrl(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        return "https://image.tmdb.org/t/p/original" + path;
    }

    @Named("mapGenres")
    default List<TmdbGenreResponse> mapGenres(List<String> genres) {
        if (genres == null) return new ArrayList<>();
        return genres.stream()
                .map(name -> TmdbGenreResponse.builder().name(name).build())
                .toList();
    }
}