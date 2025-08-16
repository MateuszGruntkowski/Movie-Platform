package com.mgrunt.movies.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Table(name = "movies")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "imdb_id", nullable = false, unique = true)
    private String imdbId;

    @Column(nullable = false)
    private String title;

    @Column
    private String releaseDate;

    @Column
    private String trailerLink;

    @Column
    private String poster;

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
    private Set<User> usersWatching = new HashSet<>();
}
