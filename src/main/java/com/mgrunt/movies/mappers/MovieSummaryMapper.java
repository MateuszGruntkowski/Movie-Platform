package com.mgrunt.movies.mappers;

import com.mgrunt.movies.domain.dtos.movie.MovieSummaryDto;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.mappers.support.TmdbUrlBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TmdbUrlBuilder.class})
public interface MovieSummaryMapper {
    @Mapping(target = "posterPath", source = "posterPath", qualifiedByName = "buildPosterUrl")
    MovieSummaryDto toMovieSummaryDto(Movie movie);
}
