package com.mgrunt.movies.services.Impl;

import com.mgrunt.movies.domain.dtos.tmdb.TmdbGenreResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbMovieDetailsResponse;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.services.TmdbService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class MovieDataAssembler {

    private static final int DEFAULT_BACKDROPS_LIMIT = 5;

    private final TmdbService tmdbService;

    Movie assemble(Movie movie, Long tmdbId) {
        TmdbMovieDetailsResponse tmdbMovie = tmdbService.fetchRawMovieDetails(tmdbId);

        movie.setTmdbId(tmdbId);
        movie.setImdbId(tmdbMovie.imdbId());
        movie.setTitle(tmdbMovie.title());
        movie.setOverview(tmdbMovie.overview());
        movie.setReleaseDate(tmdbMovie.releaseDate());
        movie.setPosterPath(tmdbMovie.posterPath());
        movie.setBackdropPath(tmdbMovie.backdropPath());
        movie.setVoteAverage(tmdbMovie.voteAverage());
        movie.setVoteCount(tmdbMovie.voteCount());
        movie.setPopularity(tmdbMovie.popularity());
        movie.setRuntime(tmdbMovie.runtime());
        movie.setOriginalLanguage(tmdbMovie.originalLanguage());
        movie.setAdult(tmdbMovie.adult());
        movie.setBudget(tmdbMovie.budget());
        movie.setRevenue(tmdbMovie.revenue());
        movie.setTagline(tmdbMovie.tagline());
        movie.setTrailerUrl(tmdbService.getTrailerUrl(tmdbId));

        movie.getBackdrops().clear();
        movie.getBackdrops().addAll(tmdbService.getImages(tmdbId, DEFAULT_BACKDROPS_LIMIT));

        List<String> genreNames = Optional.ofNullable(tmdbMovie.genres())
                .orElse(Collections.emptyList())
                .stream()
                .map(TmdbGenreResponse::name)
                .distinct()
                .toList();

        movie.getGenres().clear();
        movie.getGenres().addAll(genreNames);

        movie.setUpdatedAt(LocalDateTime.now());
        return movie;
    }
}