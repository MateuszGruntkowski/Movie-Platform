package com.mgrunt.movies.mappers;

import com.mgrunt.movies.domain.dtos.rating.RatingResponse;
import com.mgrunt.movies.domain.entities.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RatingMapper {
    RatingResponse toRatingResponse(Rating rating);
}
