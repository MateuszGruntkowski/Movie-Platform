package com.mgrunt.movies.mappers;

import com.mgrunt.movies.domain.dtos.movie.TrendingMovieResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbTrendingMovieItemResponse;
import com.mgrunt.movies.mappers.support.TmdbUrlBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {TmdbUrlBuilder.class}
)
public interface TrendingMovieMapper {
    @Mapping(target = "posterPath", source = "posterPath", qualifiedByName = "buildFullPosterUrl")
    @Mapping(target = "backdropPath", source = "backdropPath", qualifiedByName = "buildFullBackdropUrl")
    @Mapping(target = "tmdbId", source = "id")
    TrendingMovieResponse toTrendingMovie(TmdbTrendingMovieItemResponse movie);

}
