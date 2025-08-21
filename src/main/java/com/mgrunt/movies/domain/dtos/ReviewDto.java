package com.mgrunt.movies.domain.dtos;

import com.mgrunt.movies.domain.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private String authorId;
}
