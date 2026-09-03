package com.mgrunt.movies.mappers;

import com.mgrunt.movies.domain.dtos.movie.UserProfileMovieDto;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.mappers.support.TmdbUrlBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TmdbUrlBuilder.class})
public interface UserProfileMovieMapper {
    @Mapping(target = "posterPath", source = "posterPath", qualifiedByName = "buildPosterUrl")
    UserProfileMovieDto toUserProfileMovieDto(Movie movie);
}
