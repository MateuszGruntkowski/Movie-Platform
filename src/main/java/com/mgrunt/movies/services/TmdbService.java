package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.TmdbMovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.TmdbMovieSearchResult;
import com.mgrunt.movies.domain.dtos.TmdbVideoResponse;

import java.util.List;

public interface TmdbService {
    TmdbMovieDetailsResponse getMovieDetails(Long movieId);
    String getImageUrl(String imagePath, String size);
    List<TmdbVideoResponse> getMovieVideos(Long movieId);
    String getTrailerUrl(Long movieId);
    List<String> getMovieBackdrops(Long movieId, int limit);
    List<String> getCollectionBackdrops(Long collectionId);
    List<TmdbMovieSearchResult> searchMovies(String query, int limit);
}
