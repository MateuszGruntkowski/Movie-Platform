package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.documents.Movie;
import com.mgrunt.movies.domain.documents.User;
import com.mgrunt.movies.domain.dtos.MovieDto;
import com.mgrunt.movies.domain.dtos.ReviewDto;
import com.mgrunt.movies.mappers.MovieMapper;
import com.mgrunt.movies.mappers.ReviewMapper;
import com.mgrunt.movies.repositories.MovieRepository;
import com.mgrunt.movies.repositories.UserRepository;
import com.mgrunt.movies.services.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return movies.stream().map(movieMapper::toDto)
                .toList();
    }

    @Override
    public MovieDto getSingleMovie(String imdbId) {

        Movie movie = movieRepository.findMovieByImdbId(imdbId)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + imdbId));

        MovieDto movieDto = movieMapper.toDto(movie);

        List<ReviewDto> reviewDtos = movie.getReviews().stream()
                .map(review -> {
                    ReviewDto reviewDto = reviewMapper.toDto(review);
                    User author = userRepository.findById(review.getAuthorId())
                            .orElseThrow(() -> new RuntimeException("User not found with id: " + review.getAuthorId()));
                    reviewDto.setAuthorUsername(author.getUsername());
                    return reviewDto;
                }).toList();

        movieDto.setReviews(reviewDtos);
        return movieDto;

    }
}
