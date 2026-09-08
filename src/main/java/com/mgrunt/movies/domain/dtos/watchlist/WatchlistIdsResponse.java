package com.mgrunt.movies.domain.dtos.watchlist;

import java.util.Set;

public record WatchlistIdsResponse(
        Set<Long> moviesToWatchIds,
        Set<Long> moviesWatchedIds
) {
}
