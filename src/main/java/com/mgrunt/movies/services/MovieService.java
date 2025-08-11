package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.documents.Movie;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    List<Movie> getAllMovies();
    Movie getSingleMovie(String imdbId);
}
