package com.mgrunt.movies.testsupport;

import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.domain.entities.User;

import java.util.UUID;

/**
 * Shared test data builders used across unit test classes.
 * <p>
 * Keeps id/username generation consistent and in one place, and avoids
 * repeating the same "new User()/new Movie() + setters" boilerplate in
 * every test class.
 */
public final class TestFixtures {

    public static final String USERNAME = "mateusz";
    public static final UUID USER_ID = UUID.randomUUID();

    private TestFixtures() {
    }

    /**
     * A user with the default {@link #USER_ID} and {@link #USERNAME}.
     */
    public static User aUser() {
        return aUser(USER_ID, USERNAME);
    }

    public static User aUser(UUID id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    /**
     * A movie with a random id. {@code Movie.equals()} compares by id, so
     * every call returns an entity that is distinct from every other one -
     * this matters whenever a test needs two different movies in the same
     * collection or mock stub.
     */
    public static Movie aMovie() {
        Movie movie = new Movie();
        movie.setId(UUID.randomUUID());
        return movie;
    }
}