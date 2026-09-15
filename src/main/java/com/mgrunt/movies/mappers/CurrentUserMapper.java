package com.mgrunt.movies.mappers;

import com.mgrunt.movies.domain.dtos.user.CurrentUserResponse;
import com.mgrunt.movies.domain.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CurrentUserMapper {

    CurrentUserResponse toDto(User user);

}

