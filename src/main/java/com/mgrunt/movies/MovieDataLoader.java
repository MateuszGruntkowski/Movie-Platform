package com.mgrunt.movies;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgrunt.movies.domain.dtos.MovieDataDto;
import com.mgrunt.movies.domain.entities.Movie;
import com.mgrunt.movies.repositories.MovieRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class MovieDataLoader {
    private final MovieRepository movieRepository;
    private final ObjectMapper objectMapper;

    @PostConstruct
    @Transactional
    public void loadMovieData() {
        if (movieRepository.count() > 0) {
            log.info("Movies already exist in database, skipping data loading");
            return;
        }

        try {
            log.info("Loading movie data from JSON...");

            // Wczytaj JSON z resources
            InputStream inputStream = getClass().getResourceAsStream("/data/movies.json");
            if (inputStream == null) {
                throw new RuntimeException("movies.json not found in resources/data/");
            }

            // Parse JSON do MovieDto list
            List<MovieDataDto> movieDataList = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<MovieDataDto>>() {}
            );

            // Konwertuj i zapisz każdy film
            for (MovieDataDto movieData : movieDataList) {
                Movie movie = convertToEntity(movieData);
                movieRepository.save(movie);
                log.debug("Saved movie: {}", movie.getTitle());
            }

            log.info("Successfully loaded {} movies into database", movieDataList.size());

        } catch (Exception e) {
            log.error("Error loading movie data", e);
            throw new RuntimeException("Failed to load movie data", e);
        }
    }

    private Movie convertToEntity(MovieDataDto dto) {
        Movie movie = new Movie();
        movie.setImdbId(dto.getImdbId());
        movie.setTitle(dto.getTitle());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setTrailerUrl(dto.getTrailerUrl());
        movie.setPosterPath(dto.getPoster());
        movie.setGenres(new ArrayList<>(dto.getGenres()));
        movie.setBackdrops(new ArrayList<>(dto.getBackdrops()));

        return movie;
    }
}
