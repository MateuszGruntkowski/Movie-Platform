package com.mgrunt.movies.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private String id;
    private String body;
    private String createdAt;
    private String authorId;
    private String authorUsername;
}
