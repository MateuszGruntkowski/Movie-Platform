package com.mgrunt.movies.mappers;

import com.mgrunt.movies.domain.dtos.movie.MovieSearchPageResponse;
import com.mgrunt.movies.domain.dtos.movie.MovieSearchResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbMovieSearchResult;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbSearchResponse;
import com.mgrunt.movies.mappers.support.TmdbUrlBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {TmdbUrlBuilder.class}
)
public interface MovieSearchMapper {

    @Mapping(target = "posterPath", source = "posterPath", qualifiedByName = "buildFullPosterUrl")
    @Mapping(target = "backdropPath", source = "backdropPath", qualifiedByName = "buildFullBackdropUrl")
    MovieSearchResponse toMovieSearchResponse(TmdbMovieSearchResult searchResult);

    MovieSearchPageResponse toMovieSearchPageResponse(TmdbSearchResponse tmdbSearchResponse);
}