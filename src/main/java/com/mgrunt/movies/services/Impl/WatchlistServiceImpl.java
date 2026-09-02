package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.movie.WatchlistMovieResponse;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.domain.entities.User;
import com.mgrunt.movies.mappers.MovieMapper;
import com.mgrunt.movies.repositories.MovieRepository;
import com.mgrunt.movies.repositories.UserRepository;
import com.mgrunt.movies.services.MovieService;
import com.mgrunt.movies.services.WatchlistService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WatchlistServiceImpl implements WatchlistService {

    private final UserRepository userRepository;
    private final MovieService movieService;
    private final MovieMapper movieMapper;
    private final MovieRepository movieRepository;

    @Override
    public Page<WatchlistMovieResponse> getMoviesToWatch(UUID userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }
        return movieRepository.findMoviesToWatchByUserId(userId, pageable)
                .map(movieMapper::toWatchlistMovieResponse);
    }

    @Override
    public Page<WatchlistMovieResponse> getMoviesWatched(UUID userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }
        return movieRepository.findMoviesWatchedByUserId(userId, pageable)
                .map(movieMapper::toWatchlistMovieResponse);
    }

    @Override
    @Transactional
    public void toggleMovie(Long tmdbId, String listType, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Movie movie = movieService.getMovie(tmdbId);

        Set<Movie> moviesToWatch = user.getMoviesToWatch();
        Set<Movie> moviesWatched = user.getMoviesWatched();

        if (listType.equalsIgnoreCase("watched")) {
            moviesToWatch.remove(movie);
            if (moviesWatched.remove(movie)) {
                userRepository.save(user);
                return;
            }
            moviesWatched.add(movie);
        } else if (listType.equalsIgnoreCase("toWatch")) {
            moviesWatched.remove(movie);
            if (moviesToWatch.remove(movie)) {
                userRepository.save(user);
                return;
            }
            moviesToWatch.add(movie);
        } else {
            throw new IllegalArgumentException("Invalid listType: " + listType);
        }

        userRepository.save(user);
    }
}
