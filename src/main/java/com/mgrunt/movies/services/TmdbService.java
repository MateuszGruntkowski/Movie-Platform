package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.tmdb.TmdbMovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbSearchResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbVideoResponse;
import com.mgrunt.movies.domain.entities.Movie;

import java.util.List;

public interface TmdbService {
    TmdbMovieDetailsResponse fetchRawMovieDetails(Long movieId);
    List<TmdbVideoResponse> getMovieVideos(Long movieId);
    String getTrailerUrl(Long movieId);
    List<String> getMovieBackdrops(Long movieId, int limit);
    List<String> getCollectionBackdrops(Long collectionId);
    TmdbSearchResponse searchResult(String query, int page);
    /** Nadpisuje pola {@code movie} aktualnymi danymi z TMDB dla podanego tmdbId. Działa tak samo dla nowej jak i istniejącej encji. */
    void syncMovieData(Movie movie, Long tmdbId);
}