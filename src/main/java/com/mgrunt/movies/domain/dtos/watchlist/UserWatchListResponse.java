package com.mgrunt.movies.domain.dtos.watchlist;

import com.mgrunt.movies.domain.dtos.movie.MovieDetailsResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserWatchListResponse {
    private Set<MovieDetailsResponse> moviesToWatch;
    private Set<MovieDetailsResponse> moviesWatched;
}
