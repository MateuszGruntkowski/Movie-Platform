package com.mgrunt.movies.repositories;

import com.mgrunt.movies.domain.entities.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
    Optional<Rating> findByMovie_TmdbIdAndAuthorId(Long tmdbId, UUID authorId);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.author.id = :authorId")
    Double findAverageRatingByAuthorId(@Param("authorId") UUID authorId);

    @Query("SELECT r FROM Rating r " +
            "JOIN FETCH r.movie " +
            "WHERE r.author.id = :authorId")
    Page<Rating> findByAuthorId(@Param("authorId") UUID authorId, Pageable pageable);

    int countByAuthorId(UUID authorId);

}
