package com.mgrunt.movies.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "movies")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tmdb_id", nullable = false, unique = true)
    private Long tmdbId;

    @Column(name = "imdb_id", nullable = false, unique = true)
    private String imdbId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column
    private LocalDate releaseDate;

    @Column(name="trailer_url")
    private String trailerUrl;

    @Column
    private String posterPath;

    @Column(name= "backdrop_path")
    private String backdropPath;

    @Column(name = "vote_average")
    private Double voteAverage;

    @Column(name = "vote_Count")
    private Integer voteCount;

    @Column
    private Double popularity;

    @Column
    private Integer runtime;

    @ElementCollection
    @CollectionTable(name = "movie_genres", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "genre")
    private List<String> genres = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "movie_backdrops", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "backdrop")
    private List<String> backdrops = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @ManyToMany(mappedBy = "moviesToWatch")
    @JsonIgnore
    private Set<User> usersWatching = new HashSet<>();

    @ManyToMany(mappedBy = "moviesWatched")
    @JsonIgnore
    private Set<User> usersWatched = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", imdbId='" + imdbId + '\'' +
                ", title='" + title + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", trailerLink='" + trailerUrl + '\'' +
                ", poster='" + posterPath + '\'' +
                ", genres=" + genres +
                ", backdrops=" + backdrops +
                '}';
    }
}
