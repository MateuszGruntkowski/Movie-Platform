package com.mgrunt.movies.domain.dtos.watchlist;

public record WatchlistStatusDto(
        Long movieId,
        boolean inToWatch,
        boolean inWatched
) {
}