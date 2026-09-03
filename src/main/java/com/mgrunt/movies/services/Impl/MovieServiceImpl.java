package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.movie.MovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.movie.MovieSearchResponse;
import com.mgrunt.movies.domain.dtos.movie.TrendingMovieResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbSearchResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbTrendingMovieItemResponse;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.exceptions.MovieSearchException;
import com.mgrunt.movies.mappers.MovieMapper;
import com.mgrunt.movies.services.MovieService;
import com.mgrunt.movies.services.MovieSyncService;
import com.mgrunt.movies.services.TmdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {

    private final TmdbService tmdbService;
    private final MovieMapper movieMapper;
    private final MovieSyncService movieSyncService;

    @Override
    public List<TrendingMovieResponse> getTrendingMovies() {
        List<TmdbTrendingMovieItemResponse> trendingMovies = tmdbService.getTrendingMovies();
        return trendingMovies.stream().map(movieMapper::toTrendingMovie).toList();
    }

    @Override
    public MovieDetailsResponse getMovieDetails(Long tmdbId) {
        Movie movie = movieSyncService.getMovieForDisplay(tmdbId);
        return movieMapper.toMovieDetailsResponse(movie);
    }

    @Override
    public MovieSearchResponse searchMovies(String query, int page) {
        try {
            TmdbSearchResponse tmdbResponse = tmdbService.searchResult(query, page);
            return movieMapper.toMovieSearchResponse(tmdbResponse);
        } catch (Exception e) {
            log.error("Error searching movies with query: {}", query, e);
            throw new MovieSearchException("Failed to search movies with query: " + query, e);
        }
    }

    @Override
    public Movie getOrCreatePersistedMovie(Long tmdbId) {
        return movieSyncService.getOrCreatePersistedMovie(tmdbId);
    }
}