package com.mgrunt.movies.repositories;

import com.mgrunt.movies.domain.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
    Optional<Rating> findByMovie_TmdbIdAndAuthorId(Long tmdbId, UUID authorId);
}
