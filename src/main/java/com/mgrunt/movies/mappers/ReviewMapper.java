package com.mgrunt.movies.mappers;

import com.mgrunt.movies.domain.dtos.ReviewDto;
import com.mgrunt.movies.domain.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorUsername", source = "author.username")
    @Mapping(target = "createdAt", expression = "java(review.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern(\"yyyy-MM-dd\")))")
    ReviewDto toDto(Review review);

}
