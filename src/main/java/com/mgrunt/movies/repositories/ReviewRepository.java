package com.mgrunt.movies.repositories;

import com.mgrunt.movies.domain.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findByMovieId(UUID movieId);

    List<Review> findByMovieTmdbId(Long tmdbId);

    List<Review> findByAuthorId(UUID authorId);

    // Znajdź recenzje z autorami i filmami
    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.author " +
            "JOIN FETCH r.movie " +
            "WHERE r.movie.id = :movieId")
    List<Review> findByMovieIdWithAuthorAndMovie(@Param("movieId") UUID movieId);

    // Sprawdź czy user już napisał recenzję dla filmu
    boolean existsByAuthorIdAndMovieId(UUID authorId, UUID movieId);
}
