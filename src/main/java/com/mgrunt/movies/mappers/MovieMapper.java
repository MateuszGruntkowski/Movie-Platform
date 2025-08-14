package com.mgrunt.movies.mappers;

import com.mgrunt.movies.domain.documents.Movie;
import com.mgrunt.movies.domain.dtos.MovieDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    @Mapping(target = "id", expression = "java(movie.getId().toHexString())")
    @Mapping(target = "reviews", ignore = true)
    MovieDto toDto(Movie movie);
}
