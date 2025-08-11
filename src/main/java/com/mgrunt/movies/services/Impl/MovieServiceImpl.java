package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.documents.Movie;
import com.mgrunt.movies.repositories.MovieRepository;
import com.mgrunt.movies.services.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public List<Movie> allMovies() {
        return movieRepository.findAll();
    }
}
