package com.mgrunt.movies.mappers;

import com.mgrunt.movies.domain.documents.Review;
import com.mgrunt.movies.domain.dtos.ReviewDto;
import com.mgrunt.movies.repositories.UserRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "id", expression = "java(review.getId().toHexString())")
    @Mapping(target = "authorId", expression = "java(review.getAuthorId().toHexString())")
    @Mapping(target = "createdAt", expression = "java(review.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern(\"yyyy-MM-dd\")))")
    @Mapping(target = "authorUsername", ignore = true)
    ReviewDto toDto(Review review);

    List<ReviewDto> toDtoList(List<Review> reviews);
}
