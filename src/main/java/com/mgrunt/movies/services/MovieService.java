package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.documents.Movie;
import com.mgrunt.movies.domain.dtos.MovieDto;

import java.util.List;

public interface MovieService {
    List<MovieDto> getAllMovies();
    MovieDto getSingleMovie(String imdbId);
}
