package com.mgrunt.movies.services.Impl;
import com.mgrunt.movies.domain.dtos.*;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.domain.entities.Review;
import com.mgrunt.movies.domain.entities.User;
import com.mgrunt.movies.exceptions.MovieDetailsException;
import com.mgrunt.movies.exceptions.MovieSearchException;
import com.mgrunt.movies.exceptions.SearchResultsException;
import com.mgrunt.movies.mappers.MovieDetailsMapper;
import com.mgrunt.movies.mappers.MovieMapper;
import com.mgrunt.movies.mappers.ReviewMapper;
import com.mgrunt.movies.repositories.MovieRepository;
import com.mgrunt.movies.repositories.ReviewRepository;
import com.mgrunt.movies.repositories.UserRepository;
import com.mgrunt.movies.services.MovieService;
import com.mgrunt.movies.services.TmdbService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {

    private final TmdbService tmdbService;
    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;
    private final MovieDetailsMapper movieDetailsMapper;

    public List<MovieDetailsResponse> getRandomMovies() {
        int MAX_MOVIES_NUMBER = 8;
        List<Movie> movies = movieRepository.findRandomMovies(MAX_MOVIES_NUMBER);
        return movies.stream().map(movieDetailsMapper::toMovieDetailsResponse).toList();
    }

    // TMDB METHODS
    public MovieDetailsResponse getMovieDetails(Long movieId) {
        try {
            Optional<Movie> localMovie = movieRepository.findByTmdbId(movieId);

            TmdbMovieDetailsResponse tmdbData = tmdbService.getMovieDetails(movieId);
            String trailerUrl = tmdbService.getTrailerUrl(movieId);
            List<String> backdrops = tmdbService.getMovieBackdrops(movieId, 10);

            List<Review> reviews = localMovie
                    .map(reviewRepository::getReviewsByMovie)
                    .orElse(Collections.emptyList());

            return movieDetailsMapper.toMovieDetailsResponse(
                    tmdbData,
                    trailerUrl,
                    backdrops,
                    reviews
            );

        } catch (Exception e) {
            log.error("Error fetching movie details for ID: {}", movieId, e);
            throw new MovieDetailsException("Failed to fetch movie details for ID: " + movieId, e);
        }
    }

    public List<MovieSearchResponse> searchMovies(String query, int limit) {
        try {
            List<TmdbMovieSearchResult> searchResults = tmdbService.searchMovies(query, limit);
            return movieDetailsMapper.toMovieSearchResponseList(searchResults);
        } catch (Exception e) {
            log.error("Error searching movies with query: {}", query, e);
            throw new MovieSearchException("Failed to search movies with query: " + query, e);
        }
    }

    public TmdbSearchResponse getSearchResults(String query, int page){
        try{
            return tmdbService.searchResult(query, page);
        }catch(Exception e){
            log.error("Error getting search results");
            throw new SearchResultsException("Failed to get search results with query: " + query, e);
        }
    }

    @Override
    public Movie findOrCreateMovie(Long tmdbId) {
        Optional<Movie> existingMovie = movieRepository.findByTmdbId(tmdbId);
        if (existingMovie.isPresent()) {
            return existingMovie.get();
        }

        try {
            Movie movieFromTmdb = tmdbService.createMovieFromTmdbData(tmdbId);
            return movieRepository.save(movieFromTmdb);
        } catch (Exception e) {
            log.error("Failed to fetch movie from TMDB for tmdbId: {}", tmdbId, e);
            throw new RuntimeException("Failed to fetch movie from TMDB for tmdbId: " + tmdbId, e);
        }
    }

}
