package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.MovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.MovieDto;
import com.mgrunt.movies.domain.dtos.UserWatchListResponse;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.domain.entities.User;
import com.mgrunt.movies.mappers.MovieDetailsMapper;
import com.mgrunt.movies.mappers.MovieMapper;
import com.mgrunt.movies.repositories.MovieRepository;
import com.mgrunt.movies.repositories.UserRepository;
import com.mgrunt.movies.services.MovieService;
import com.mgrunt.movies.services.WatchlistService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
    private final MovieDetailsMapper movieDetailsMapper;

    @Override
    public UserWatchListResponse getWatchlist(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Set<MovieDetailsResponse> moviesWatched = user.getMoviesWatched().stream()
                .map(movieDetailsMapper::toMovieDetailsResponse)
                .collect(Collectors.toSet());

        Set<MovieDetailsResponse> moviesToWatch = user.getMoviesToWatch().stream()
                .map(movieDetailsMapper::toMovieDetailsResponse)
                .collect(Collectors.toSet());

        return UserWatchListResponse.builder()
                .moviesToWatch(moviesToWatch)
                .moviesWatched(moviesWatched)
                .build();
    }

    @Transactional
    public void toggleMovie(Long tmdbId, String listType, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Znajdź lub utwórz film
        Movie movie = movieService.findOrCreateMovie(tmdbId);

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
