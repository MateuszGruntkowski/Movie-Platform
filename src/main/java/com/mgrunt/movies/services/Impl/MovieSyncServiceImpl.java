package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.repositories.MovieRepository;
import com.mgrunt.movies.services.MovieSyncService;
import com.mgrunt.movies.services.TmdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieSyncServiceImpl implements MovieSyncService {
    private static final long CACHE_TTL_HOURS = 24;

    private final TmdbService tmdbService;
    private final MovieRepository movieRepository;

    @Transactional
    public Movie getOrSyncMovie(Long tmdbId) {
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
