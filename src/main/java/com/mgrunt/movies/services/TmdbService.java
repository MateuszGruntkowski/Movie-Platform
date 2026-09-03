package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.tmdb.TmdbMovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbSearchResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbTrendingMovieItemResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbVideoItemResponse;
import com.mgrunt.movies.domain.entities.Movie;
import java.util.List;

public interface TmdbService {
    TmdbMovieDetailsResponse fetchRawMovieDetails(Long movieId);
    List<TmdbVideoItemResponse> getMovieVideos(Long movieId);
    List<TmdbTrendingMovieItemResponse> getTrendingMovies();
    String getTrailerUrl(Long movieId);
    List<String> getImages(Long movieId, int limit);
    TmdbSearchResponse searchResult(String query, int page);
}