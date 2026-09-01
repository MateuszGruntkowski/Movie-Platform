package com.mgrunt.movies.repositories;

import com.mgrunt.movies.domain.entities.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {
    @Query("""
            SELECT m FROM Movie m
            WHERE m IN (SELECT m2 FROM User u JOIN u.moviesToWatch m2 WHERE u.id = :userId)
            """)
    Page<Movie> findMoviesToWatchByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
            SELECT m FROM Movie m
            WHERE m IN (SELECT m2 FROM User u JOIN u.moviesWatched m2 WHERE u.id = :userId)
            """)
    Page<Movie> findMoviesWatchedByUserId(@Param("userId") UUID userId, Pageable pageable);

    Optional<Movie> findByTmdbId(Long tmdbId);

}
