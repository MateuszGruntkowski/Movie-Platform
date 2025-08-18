package com.mgrunt.movies.mappers;

import com.mgrunt.movies.domain.dtos.MovieDto;
import com.mgrunt.movies.domain.entities.Movie;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;

@Mapper(componentModel = "spring", uses = ReviewMapper.class)
public interface MovieMapper {

    MovieDto toDto(Movie movie);

    @Mapping(target = "reviews", ignore = true)
    @Named("movieToDtoWithoutReviews")
    MovieDto toDtoWithoutReviews(Movie movie);
}
