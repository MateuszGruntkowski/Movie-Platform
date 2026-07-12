package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.*;
import com.mgrunt.movies.domain.entities.Movie;

import java.util.List;

public interface TmdbService {
    TmdbMovieDetailsResponse getMovieDetails(Long movieId);
    List<TmdbVideoResponse> getMovieVideos(Long movieId);
    String getTrailerUrl(Long movieId);
    List<String> getMovieBackdrops(Long movieId, int limit);
    List<String> getCollectionBackdrops(Long collectionId);
    List<TmdbMovieSearchResult> searchMovies(String query, int limit);
    TmdbSearchResponse searchResult(String query, int page);
    Movie createMovieFromTmdbData(Long movieId);
}