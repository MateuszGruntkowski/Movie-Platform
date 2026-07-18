package com.mgrunt.movies.repositories;

import com.mgrunt.movies.domain.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
    Optional<Rating> findByMovie_TmdbIdAndAuthorId(Long tmdbId, UUID authorId);
    Set<Rating> findByAuthorId(UUID authorId);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.author.id = :authorId")
    Double findAverageRatingByAuthorId(@Param("authorId") UUID authorId);

    Integer findRatingsCountByAuthorId(UUID authorId);
}
