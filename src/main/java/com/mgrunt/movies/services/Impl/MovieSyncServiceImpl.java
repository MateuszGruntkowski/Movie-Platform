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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieSyncServiceImpl implements MovieSyncService {
    private static final long CACHE_TTL_HOURS = 24;

//    private final TmdbService tmdbService;
    private final MovieRepository movieRepository;
    private final MovieDataAssembler movieDataAssembler;

    @Override
    @Transactional
    public Movie getOrCreatePersistedMovie(Long tmdbId) {
        Movie movie = movieRepository.findByTmdbId(tmdbId).orElseGet(Movie::new);
        if (isStale(movie)) {
            movieDataAssembler.assemble(movie, tmdbId);
            movie = movieRepository.save(movie);
        }
        return movie;
    }

    @Override
    @Transactional
    public Movie getMovieForDisplay(Long tmdbId) {
        Optional<Movie> existing = movieRepository.findByTmdbId(tmdbId);

        if (existing.isEmpty()) {
            Movie transientMovie = new Movie();
            return movieDataAssembler.assemble(transientMovie, tmdbId); // brak save()
        }

        Movie movie = existing.get();
        if (isStale(movie)) {
            movieDataAssembler.assemble(movie, tmdbId);
            movieRepository.save(movie);
        }
        return movie;
    }

    private boolean isStale(Movie movie) {
        return movie.getUpdatedAt() == null
                || movie.getUpdatedAt().isBefore(LocalDateTime.now().minusHours(CACHE_TTL_HOURS));
    }
}