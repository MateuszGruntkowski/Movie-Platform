package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.documents.Movie;
import com.mgrunt.movies.repositories.MovieRepository;
import com.mgrunt.movies.services.MovieService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Movie getSingleMovie(String imdbId) {
        return movieRepository.findMovieByImdbId(imdbId)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + imdbId));
    }
}
