package com.mgrunt.movies.mappers;

import com.mgrunt.movies.domain.dtos.profile.ProfileRatingDto;
import com.mgrunt.movies.domain.dtos.rating.RatingDto;
import com.mgrunt.movies.domain.entities.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {MovieSummaryMapper.class}
)
public interface RatingMapper {
    RatingDto toRatingDto(Rating rating);
    ProfileRatingDto toProfileRatingDto(Rating rating);
}
