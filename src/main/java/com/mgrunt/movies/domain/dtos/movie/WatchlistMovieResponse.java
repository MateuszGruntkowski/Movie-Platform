package com.mgrunt.movies.domain.dtos.movie;

import com.mgrunt.movies.domain.dtos.tmdb.TmdbGenreResponse;

import java.time.LocalDate;
import java.util.List;

public record WatchlistMovieResponse(
        Long tmdbId,
        String title,
        LocalDate releaseDate,
        String posterPath,
        Double voteAverage,
        List<TmdbGenreResponse> genres
) {
}
