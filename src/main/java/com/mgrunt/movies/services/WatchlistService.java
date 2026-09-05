package com.mgrunt.movies.services;

import com.mgrunt.movies.domain.dtos.movie.WatchlistMovieResponse;
import com.mgrunt.movies.domain.dtos.watchlist.WatchlistStatusDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface WatchlistService {

    Page<WatchlistMovieResponse> getMoviesToWatch(UUID userId, Pageable pageable);
    Page<WatchlistMovieResponse> getMoviesWatched(UUID userId, Pageable pageable);
    WatchlistStatusDto toggleMovie(Long tmdbId, String listType, UUID userId);
}
