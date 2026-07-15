package com.mgrunt.movies.mappers;

import com.mgrunt.movies.domain.dtos.movie.MovieDetailsResponse;
import com.mgrunt.movies.domain.dtos.tmdb.TmdbGenreResponse;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.mappers.support.TmdbUrlBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ReviewMapper.class, TmdbUrlBuilder.class}
)
public interface MovieDetailsMapper {

    @Mapping(target = "backdrops", source = "backdrops", qualifiedByName = "buildFullBackdropUrl")
    @Mapping(target = "genres", source = "genres", qualifiedByName = "mapGenres")
    @Mapping(target = "belongsToCollection", ignore = true)
    @Mapping(target = "posterPath", source = "posterPath", qualifiedByName = "buildFullPosterUrl")
    @Mapping(target = "backdropPath", source = "backdropPath", qualifiedByName = "buildFullBackdropUrl")
    MovieDetailsResponse toMovieDetailsResponse(Movie movie);

    @Named("mapGenres")
    default List<TmdbGenreResponse> mapGenres(List<String> genres) {
        if (genres == null) return new ArrayList<>();
        return genres.stream()
                .map(name -> TmdbGenreResponse.builder().name(name).build())
                .toList();
    }
}