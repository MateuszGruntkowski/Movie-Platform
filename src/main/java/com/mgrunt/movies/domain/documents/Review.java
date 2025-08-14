package com.mgrunt.movies.domain.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;

@Document(collection = "reviews")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    @Id
    private ObjectId id;
    private String body;
    private LocalDateTime createdAt = LocalDateTime.now();
    private ObjectId authorId;

    public Review(String body) {
        this.body = body;
    }

    public Review(String body, ObjectId authorId) {
        this.body = body;
        this.authorId = authorId;
    }
}
