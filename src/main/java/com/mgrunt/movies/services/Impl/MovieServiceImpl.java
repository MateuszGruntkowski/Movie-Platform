package com.mgrunt.movies.services.Impl;
import com.mgrunt.movies.domain.dtos.MovieDto;
import com.mgrunt.movies.domain.dtos.ReviewDto;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.domain.entities.User;
import com.mgrunt.movies.mappers.MovieMapper;
import com.mgrunt.movies.mappers.ReviewMapper;
import com.mgrunt.movies.repositories.MovieRepository;
import com.mgrunt.movies.repositories.UserRepository;
import com.mgrunt.movies.services.MovieService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;

    @Override
    public List<MovieDto> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();

        // return without reviews
        return movies.stream().map(movieMapper::toDtoWithoutReviews)
                .toList();
    }

    @Override
    public MovieDto getSingleMovie(String imdbId) {

        Movie movie = movieRepository.findByImdbIdWithReviewsAndAuthors(imdbId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with id: " + imdbId));

        return movieMapper.toDto(movie);

    }

//    @Transactional
//    @Override
//    public void addMovieToWatchlist(UUID userId, String imdbId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        Movie movie = movieRepository.findByImdbId(imdbId)
//                .orElseThrow(() -> new RuntimeException("Movie not found"));
//
//        user.getMoviesToWatch().add(movie);
//        userRepository.save(user);
//    }
}
