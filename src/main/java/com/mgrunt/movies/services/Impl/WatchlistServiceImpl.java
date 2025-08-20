package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.MovieDto;
import com.mgrunt.movies.domain.dtos.UserWatchListResponse;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.domain.entities.User;
import com.mgrunt.movies.mappers.MovieMapper;
import com.mgrunt.movies.repositories.MovieRepository;
import com.mgrunt.movies.repositories.UserRepository;
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
    private final MovieMapper movieMapper;
    private final MovieRepository movieRepository;

    @Override
    public UserWatchListResponse getWatchlist(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Set<MovieDto> moviesWatched = user.getMoviesWatched().stream()
                .map(movieMapper::toDtoWithoutReviews)
                .collect(Collectors.toSet());

        Set<MovieDto> moviesToWatch = user.getMoviesToWatch().stream()
                .map(movieMapper::toDtoWithoutReviews)
                .collect(Collectors.toSet());

        return UserWatchListResponse.builder()
                .moviesToWatch(moviesToWatch)
                .moviesWatched(moviesWatched)
                .build();
    }

    @Override
    @Transactional
    public void removeFromToWatch(Authentication authentication, UUID movieId) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.getMoviesToWatch().removeIf(movie -> movie.getId().equals(movieId));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeFromWatched(Authentication authentication, UUID movieId) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.getMoviesWatched().removeIf(movie -> movie.getId().equals(movieId));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void markAsToWatch(UUID movieId, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        user.getMoviesWatched().removeIf(m -> m.getId().equals(movieId));
        user.getMoviesToWatch().add(movie);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void markAsWatched(UUID movieId, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        user.getMoviesToWatch().removeIf(m -> m.getId().equals(movieId));
        user.getMoviesWatched().add(movie);
        userRepository.save(user);

    }

    @Override
    @Transactional
    public void toggleMovie(UUID movieId, String listType, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        Set<Movie> moviesToWatch = user.getMoviesToWatch();
        Set<Movie> moviesWatched = user.getMoviesWatched();

        if (listType.equalsIgnoreCase("watched")) {
            // Usuń z "do obejrzenia", jeśli jest
            moviesToWatch.remove(movie);

            // Jeśli już był w obejrzanych → usuń go (toggle off)
            if (moviesWatched.remove(movie)) {
                userRepository.save(user);
                return;
            }

            // W przeciwnym razie dodaj do obejrzanych
            moviesWatched.add(movie);
        } else if (listType.equalsIgnoreCase("toWatch")) {
            // Usuń z obejrzanych, jeśli jest
            moviesWatched.remove(movie);

            // Jeśli już był na liście do obejrzenia → usuń go (toggle off)
            if (moviesToWatch.remove(movie)) {
                userRepository.save(user);
                return;
            }

            // W przeciwnym razie dodaj do listy do obejrzenia
            moviesToWatch.add(movie);
        } else {
            throw new IllegalArgumentException("Invalid listType: " + listType);
        }

        userRepository.save(user);
    }
}
