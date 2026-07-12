package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.movie.MovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.movie.MovieSearchPageResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbMovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbSearchResponse;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.domain.entities.Review;
import com.mgrunt.movies.exceptions.MovieDetailsException;
import com.mgrunt.movies.exceptions.MovieSearchException;
import com.mgrunt.movies.mappers.MovieDetailsMapper;
import com.mgrunt.movies.repositories.MovieRepository;
import com.mgrunt.movies.repositories.ReviewRepository;
import com.mgrunt.movies.services.MovieService;
import com.mgrunt.movies.services.TmdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {

    private static final int RANDOM_MOVIES_COUNT = 8;
    private static final int DEFAULT_BACKDROPS_LIMIT = 10;

    private final TmdbService tmdbService;
    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;
    private final MovieDetailsMapper movieDetailsMapper;

    @Override
    public List<MovieDetailsResponse> getRandomMovies() {
        List<Movie> movies = movieRepository.findRandomMovies(RANDOM_MOVIES_COUNT);
        return movies.stream().map(movieDetailsMapper::toMovieDetailsResponse).toList();
    }

    @Override
    public MovieDetailsResponse getMovieDetails(Long movieId) {
        try {
            Optional<Movie> localMovie = movieRepository.findByTmdbId(movieId);

            TmdbMovieDetailsResponse tmdbData = tmdbService.getMovieDetails(movieId);
            String trailerUrl = tmdbService.getTrailerUrl(movieId);
            List<String> backdrops = tmdbService.getMovieBackdrops(movieId, DEFAULT_BACKDROPS_LIMIT);

            List<Review> reviews = localMovie
                    .map(reviewRepository::getReviewsByMovie)
                    .orElse(Collections.emptyList());

            return movieDetailsMapper.toMovieDetailsResponse(tmdbData, trailerUrl, backdrops, reviews);
        } catch (Exception e) {
            log.error("Error fetching movie details for ID: {}", movieId, e);
            throw new MovieDetailsException("Failed to fetch movie details for ID: " + movieId, e);
        }
    }

    @Override
    public MovieSearchPageResponse searchMovies(String query, int page) {
        try {
            TmdbSearchResponse tmdbResponse = tmdbService.searchResult(query, page);
            return movieDetailsMapper.toMovieSearchPageResponse(tmdbResponse);
        } catch (Exception e) {
            log.error("Error searching movies with query: {}", query, e);
            throw new MovieSearchException("Failed to search movies with query: " + query, e);
        }
    }

    @Override
    public Movie findOrCreateMovie(Long tmdbId) {
        return movieRepository.findByTmdbId(tmdbId)
                .orElseGet(() -> {
                    try {
                        Movie movieFromTmdb = tmdbService.createMovieFromTmdbData(tmdbId);
                        return movieRepository.save(movieFromTmdb);
                    } catch (Exception e) {
                        log.error("Failed to fetch movie from TMDB for tmdbId: {}", tmdbId, e);
                        throw new RuntimeException("Failed to fetch movie from TMDB for tmdbId: " + tmdbId, e);
                    }
                });
    }
}