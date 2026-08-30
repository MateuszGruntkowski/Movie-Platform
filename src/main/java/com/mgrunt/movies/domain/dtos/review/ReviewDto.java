package com.mgrunt.movies.domain.dtos.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto {
    private UUID id;
    private String body;
    private String createdAt;
    private String authorUsername;
    private String authorAvatarPath;
    private String authorId;
}
