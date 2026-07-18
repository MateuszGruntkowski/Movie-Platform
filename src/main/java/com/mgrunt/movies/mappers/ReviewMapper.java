package com.mgrunt.movies.mappers;

import com.mgrunt.movies.domain.dtos.profile.ProfileReviewDto;
import com.mgrunt.movies.domain.dtos.review.ReviewDto;
import com.mgrunt.movies.domain.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {MovieSummaryMapper.class}
)
public interface ReviewMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorUsername", source = "author.username")
    @Mapping(target = "createdAt", expression = "java(review.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern(\"yyyy-MM-dd\")))")
    ReviewDto toDto(Review review);

    ProfileReviewDto toProfileReviewDto(Review review);
}
