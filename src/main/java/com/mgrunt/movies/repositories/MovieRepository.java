package com.mgrunt.movies.repositories;

import com.mgrunt.movies.domain.entities.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {

    Optional<Movie> findByImdbId(String imdbId);

    // Znajdź film z recenzjami i autorami (rozwiąże Twój główny problem!)
    @Query("SELECT m FROM Movie m " +
            "LEFT JOIN FETCH m.reviews r " +
            "LEFT JOIN FETCH r.author " +
            "WHERE m.imdbId = :imdbId")
    Optional<Movie> findByImdbIdWithReviewsAndAuthors(@Param("imdbId") String imdbId);

    // Znajdź filmy z gatunku
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.genres g WHERE g = :genre")
    List<Movie> findByGenre(@Param("genre") String genre);

    @Query(value = "SELECT * FROM Movies ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Movie> findRandomMovies(@Param("limit") int limit);

    boolean existsByImdbId(String imdbId);

//    Optional<Movie> findByTmdbId(String movieId);
    Optional<Movie> findByTmdbId(Long tmdbId);

    // Znajdź najlepiej oceniane filmy (jeśli dodasz rating do Review)
//    @Query("SELECT m FROM Movie m " +
//            "LEFT JOIN m.reviews r " +
//            "GROUP BY m.id " +
//            "ORDER BY AVG(r.rating) DESC")
//    List<Movie> findTopRatedMovies();
}
