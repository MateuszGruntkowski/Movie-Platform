package com.mgrunt.movies.mappers;

import com.mgrunt.movies.domain.dtos.movie.*;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbGenreResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbMovieSearchItemResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbSearchResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbTrendingMovieItemResponse;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.mappers.support.TmdbUrlBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ReviewMapper.class, TmdbUrlBuilder.class}
)
public interface MovieMapper {

    @Mapping(target = "backdrops", source = "backdrops", qualifiedByName = "buildBackdropUrl")
    @Mapping(target = "genres", source = "genres", qualifiedByName = "mapGenres")
    @Mapping(target = "posterPath", source = "posterPath", qualifiedByName = "buildPosterUrl")
    @Mapping(target = "backdropPath", source = "backdropPath", qualifiedByName = "buildBackdropUrl")
    MovieDetailsResponse toMovieDetailsResponse(Movie movie);

    @Mapping(target = "posterPath", source = "posterPath", qualifiedByName = "buildPosterUrl")
    @Mapping(target = "backdropPath", source = "backdropPath", qualifiedByName = "buildBackdropUrl")
    MovieSearchItemResponse toMovieSearchItemResponse(TmdbMovieSearchItemResponse searchResult);

    MovieSearchResponse toMovieSearchResponse(TmdbSearchResponse tmdbSearchResponse);

    @Mapping(target = "posterPath", source = "posterPath", qualifiedByName = "buildPosterUrl")
    @Mapping(target = "backdropPath", source = "backdropPath", qualifiedByName = "buildFullTrendingMovieBackdropUrl")
    @Mapping(target = "tmdbId", source = "id")
    TrendingMovieResponse toTrendingMovie(TmdbTrendingMovieItemResponse movie);

    @Mapping(target = "posterPath", source = "posterPath", qualifiedByName = "buildPosterUrl")
    @Mapping(target = "genres", source = "genres", qualifiedByName = "mapGenres")
    WatchlistMovieResponse toWatchlistMovieResponse(Movie movie);

    @Named("mapGenres")
    default List<TmdbGenreResponse> mapGenres(List<String> genres) {
        if (genres == null) return new ArrayList<>();
        return genres.stream()
                .map(name -> TmdbGenreResponse.builder().name(name).build())
                .toList();
    }
}