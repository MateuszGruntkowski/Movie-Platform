package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.documents.Movie;

import java.util.List;

public interface MovieService {
    List<Movie> allMovies();
}
