package com.mgrunt.movies.repositories;

import com.mgrunt.movies.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.reviews " +
            "LEFT JOIN FETCH u.moviesToWatch " +
            "LEFT JOIN FETCH u.moviesWatched " +
            "WHERE u.username = :username")
    Optional<User> findByUsernameWithDetails(@Param("username") String username);

    // Znajdź użytkownika z watchlistą
    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.moviesToWatch " +
            "WHERE u.id = :userId")
    Optional<User> findByIdWithWatchlist(@Param("userId") UUID userId);

    // Znajdź użytkownika z recenzjami
    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.reviews r " +
            "LEFT JOIN FETCH r.movie " +
            "WHERE u.id = :userId")
    Optional<User> findByIdWithReviews(@Param("userId") UUID userId);
}
