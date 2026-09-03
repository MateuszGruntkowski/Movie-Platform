package com.mgrunt.movies.mappers;

import com.mgrunt.movies.domain.dtos.user.UserDto;
import com.mgrunt.movies.domain.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ReviewMapper.class})
public interface UserMapper {

    @Mapping(target = "moviesToWatchIds", expression = "java(user.getMoviesToWatch().stream().map(movie -> movie.getTmdbId()).collect(java.util.stream.Collectors.toSet()))")
    @Mapping(target = "moviesWatchedIds", expression = "java(user.getMoviesWatched().stream().map(movie -> movie.getTmdbId()).collect(java.util.stream.Collectors.toSet()))")
    UserDto toDto(User user);

}

