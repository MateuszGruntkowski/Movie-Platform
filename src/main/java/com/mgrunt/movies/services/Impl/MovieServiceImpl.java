package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.movie.MovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.movie.MovieSearchResponse;
import com.mgrunt.movies.domain.dtos.movie.TrendingMovieResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbSearchResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbTrendingMovieItemResponse;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.exceptions.MovieSearchException;
import com.mgrunt.movies.mappers.MovieDetailsMapper;
import com.mgrunt.movies.mappers.MovieSearchMapper;
import com.mgrunt.movies.mappers.TrendingMovieMapper;
import com.mgrunt.movies.repositories.MovieRepository;
import com.mgrunt.movies.services.MovieService;
import com.mgrunt.movies.services.MovieSyncService;
import com.mgrunt.movies.services.TmdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {

    private static final int RANDOM_MOVIES_COUNT = 8;

    private final TmdbService tmdbService;
    private final MovieRepository movieRepository;
    private final MovieDetailsMapper movieDetailsMapper;
    private final MovieSearchMapper movieSearchMapper;
    private final MovieSyncService movieSyncService;
    private final TrendingMovieMapper trendingMovieMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MovieDetailsResponse> getRandomMovies() {
        List<Movie> movies = movieRepository.findRandomMovies(RANDOM_MOVIES_COUNT);
        return movies.stream().map(movieDetailsMapper::toMovieDetailsResponse).toList();
    }

    @Override
    public List<TrendingMovieResponse> getTrendingMovies() {
        List<TmdbTrendingMovieItemResponse> trendingMovies = tmdbService.getTrendingMovies();
        return trendingMovies.stream().map(trendingMovieMapper::toTrendingMovie).toList();
    }

    @Override
    public MovieDetailsResponse getMovieDetails(Long tmdbId) {
        Movie movie = movieSyncService.getMovieForView(tmdbId);
        return movieDetailsMapper.toMovieDetailsResponse(movie);
    }

    @Override
    public MovieSearchResponse searchMovies(String query, int page) {
        try {
            TmdbSearchResponse tmdbResponse = tmdbService.searchResult(query, page);
            return movieSearchMapper.toMovieSearchResponse(tmdbResponse);
        } catch (Exception e) {
            log.error("Error searching movies with query: {}", query, e);
            throw new MovieSearchException("Failed to search movies with query: " + query, e);
        }
    }

    @Override
    public Movie getMovie(Long tmdbId) {
        return movieSyncService.getOrSyncMovie(tmdbId);
    }
}