import React from "react";
import "./MovieCard.css";
import { movieDetailsService } from "../../Services/movieDetailsService";

const MovieCard = ({ movie }) => {
  if (!movie) return null;

  const formatReleaseDate = (releaseDate) => {
    if (!releaseDate) return null;
    try {
      return new Date(releaseDate).getFullYear();
    } catch {
      return releaseDate;
    }
  };

  const renderGenres = (genres, limit = 3) => {
    if (!genres || !Array.isArray(genres) || genres.length === 0) return null;

    const visibleGenres = genres.slice(0, limit);
    const hiddenCount = genres.length - limit;

    return (
      <div className="movie-genres">
        {visibleGenres.map((genre, index) => (
          <span key={genre.id || index} className="genre-tag">
            {genre.name || genre}
          </span>
        ))}
        {hiddenCount > 0 && (
          <span className="genre-more">+{hiddenCount} more</span>
        )}
      </div>
    );
  };

  return (
    <div className="movie-poster-container">
      {console.log("MovieCard movie:", movie)}
      <img
        src={
          movieDetailsService.getImageUrl(movie.poster_path) ||
          movie.poster ||
          "https://via.placeholder.com/300x450/2A2D3A/FFD700?text=No+Poster"
        }
        alt={movie.title || "Movie poster"}
        className="movie-poster"
      />
      <div className="movie-info">
        <h3 className="movie-title">{movie.title}</h3>
        {movie.release_date && (
          <div className="movie-year">
            {formatReleaseDate(movie.release_date || movie.releaseDate)}
          </div>
        )}
        {renderGenres(movie.genres)}
      </div>
    </div>
  );
};

export default MovieCard;
