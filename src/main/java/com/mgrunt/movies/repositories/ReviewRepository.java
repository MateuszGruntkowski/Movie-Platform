package com.mgrunt.movies.repositories;

import com.mgrunt.movies.domain.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.author " +
            "WHERE r.movie.tmdbId = :tmdbId")
    Page<Review> findByMovieTmdbId(@Param("tmdbId") Long tmdbId, Pageable pageable);

    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.movie " +
            "WHERE r.author.id = :authorId")
    Page<Review> findByAuthorId(@Param("authorId") UUID authorId, Pageable pageable);

    int countByAuthorId(UUID authorId);

}
