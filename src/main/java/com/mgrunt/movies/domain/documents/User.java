package com.mgrunt.movies.domain.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Set;

@Document(collection = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    private ObjectId id;
    private String username;
    private String password;
    private String email;

    @DocumentReference
    private Set<Movie> moviesToWatch;

    @DocumentReference
    @Field("reviewIds")
    private List<Review> reviews;

}
