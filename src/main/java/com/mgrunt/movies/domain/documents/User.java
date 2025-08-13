package com.mgrunt.movies.domain.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User {

    @Id
    private ObjectId id;
    private String username;
    private String password;
    private LocalDateTime createdAt;

    @DocumentReference
    @Field("movieIds")
    private Set<Movie> moviesToWatch = new HashSet<>();

    @DocumentReference
    @Field("reviewIds")
    private List<Review> reviews = new ArrayList<>();

}
