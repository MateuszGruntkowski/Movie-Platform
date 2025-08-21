// MovieCard.js
import React from "react";
import { CheckCircle, Clock, X } from "lucide-react";
import { Link } from "react-router-dom";

const MovieCard = ({
  movie,
  listType,
  onMarkAsWatched,
  onMarkAsToWatch,
  onRemove,
}) => (
  <div className="wl-movie-card">
    <Link to={`/Details/${movie.tmdbId}`} title={movie.title || "See more!"}>
      <div className="wl-movie-poster">
        <img src={movie.posterPath} alt={movie.title} />
        <div className="wl-movie-overlay">
          <div className="wl-movie-actions">
            {listType === "moviesToWatch" ? (
              <button
                className="wl-action-btn wl-watch-btn"
                onClick={() => onMarkAsWatched(movie.imdbId)}
                title="Mark as watched"
              >
                <CheckCircle size={20} />
              </button>
            ) : (
              <button
                className="wl-action-btn wl-unwatch-btn"
                onClick={() => onMarkAsToWatch(movie.imdbId)}
                title="Move to watchlist"
              >
                <Clock size={20} />
              </button>
            )}
            <button
              className="wl-action-btn wl-remove-btn"
              onClick={() => onRemove(movie.imdbId)}
              title="Remove from list"
            >
              <X size={20} />
            </button>
          </div>
        </div>
      </div>
    </Link>

    <div className="wl-movie-info">
      <h3 className="wl-movie-title">{movie.title}</h3>
      <p className="wl-movie-year">
        {new Date(movie.releaseDate).getFullYear()}
      </p>
      <div className="wl-movie-genres">
        {movie.genres.slice(0, 3).map((genre, index) => (
          <span key={index} className="wl-genre-tag">
            {genre.name}
          </span>
        ))}
        {movie.genres.length > 3 && (
          <span className="wl-genre-more">+{movie.genres.length - 3} more</span>
        )}
      </div>
    </div>
  </div>
);

export default MovieCard;
