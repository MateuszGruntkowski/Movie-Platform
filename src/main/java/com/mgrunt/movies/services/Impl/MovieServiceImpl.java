package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.movie.MovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.movie.MovieSearchPageResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbSearchResponse;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.exceptions.MovieSearchException;
import com.mgrunt.movies.mappers.MovieDetailsMapper;
import com.mgrunt.movies.repositories.MovieRepository;
import com.mgrunt.movies.services.MovieService;
import com.mgrunt.movies.services.TmdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {

    private static final int RANDOM_MOVIES_COUNT = 8;
    private static final long CACHE_TTL_HOURS = 24;

    private final TmdbService tmdbService;
    private final MovieRepository movieRepository;
    private final MovieDetailsMapper movieDetailsMapper;

    @Override
    public List<MovieDetailsResponse> getRandomMovies() {
        List<Movie> movies = movieRepository.findRandomMovies(RANDOM_MOVIES_COUNT);
        return movies.stream().map(movieDetailsMapper::toMovieDetailsResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MovieDetailsResponse getMovieDetails(Long tmdbId) {
        Movie movie = getMovie(tmdbId);
        return movieDetailsMapper.toMovieDetailsResponse(movie);
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
    @Transactional
    public Movie getMovie(Long tmdbId) {
        Movie movie = movieRepository.findByTmdbId(tmdbId)
                .orElseGet(Movie::new);

        if (isStale(movie)) {
            tmdbService.syncMovieData(movie, tmdbId);
            return movieRepository.save(movie);
        }
        return movie;
    }

    private boolean isStale(Movie movie) {
        return movie.getUpdatedAt() == null
                || movie.getUpdatedAt().isBefore(LocalDateTime.now().minusHours(CACHE_TTL_HOURS));
    }
}