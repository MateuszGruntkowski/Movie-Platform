import { Link } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCirclePlay } from "@fortawesome/free-solid-svg-icons";
import { Container, Row, Col } from "react-bootstrap";
import { CheckCircle, Clock } from "lucide-react";
import "./MovieCard.css";
import { useUser } from "../context/UserContext";
import { usePopup } from "../../hooks/usePopup";

const MovieCard = ({ movie, isLoading, showPopup }) => {
  if (!movie) return null;

  const { toggleMovieStatus, isWatched, isToWatch } = useUser();

  const handleWatchlistClick = async (movieId, listType) => {
    await toggleMovieStatus(movieId, listType, showPopup);
  };

  const formatReleaseDate = (releaseDate) => {
    if (!releaseDate) return null;
    try {
      return new Date(releaseDate).getFullYear();
    } catch {
      return releaseDate;
    }
  };

  const formatRuntime = (runtime) => {
    if (!runtime) return null;
    const hours = Math.floor(runtime / 60);
    const minutes = runtime % 60;
    return `${hours}h ${minutes}m`;
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
          movie.posterPath ||
          "https://via.placeholder.com/300x450/2A2D3A/FFD700?text=No+Poster"
        }
        alt={movie.title || "Movie poster"}
        className="movie-poster"
      />
      <div className="movie-info">
        <h3 className="movie-title">{movie.title}</h3>
        {movie.releaseDate && (
          <div className="movie-year">
            {formatReleaseDate(movie.releaseDate)}
          </div>
        )}

        {movie.overview && (
          <div className="movie-overview">{movie.overview}</div>
        )}

        <div className="movie-details">
          {movie.voteAverage && (
            <div className="movie-rating">
              <span className="rating-value">
                ★ {movie.voteAverage.toFixed(1)}
              </span>
              {movie.voteCount && (
                <span className="vote-count">({movie.voteCount} votes)</span>
              )}
            </div>
          )}

          {movie.runtime && (
            <div className="movie-runtime">{formatRuntime(movie.runtime)}</div>
          )}
        </div>

        {renderGenres(movie.genres)}

        {/* Trailer button */}
        {movie.trailerUrl && (
          <div className="trailer-section">
            <Link
              to={`/Trailer/${movie.trailerUrl.substring(
                movie.trailerUrl.length - 11
              )}`}
            >
              <div className="play-button-icon-container">
                <FontAwesomeIcon
                  className="play-button-icon"
                  icon={faCirclePlay}
                />
              </div>
            </Link>
          </div>
        )}

        {/*Watch list buttons*/}
        {isLoading ? (
          <div>Loading...</div>
        ) : (
          <div className="watchlist-buttons-container">
            <button
              className={`watchlist-button watched-button ${
                isWatched(movie.tmdbId) ? "active" : ""
              }`}
              onClick={(e) => {
                e.stopPropagation();
                handleWatchlistClick(movie.tmdbId, "watched");
                console.log(movie.tmdbId);
                console.log(typeof movie.tmdbId);
              }}
              title="Mark as watched"
            >
              <CheckCircle size={20} />
              <span>Watched</span>
            </button>

            <button
              className={`watchlist-button to-watch-button ${
                isToWatch(movie.tmdbId) ? "active" : ""
              }`}
              onClick={(e) => {
                e.stopPropagation();
                handleWatchlistClick(movie.tmdbId, "toWatch");
              }}
              title="Mark as to watch"
            >
              <Clock size={20} />
              <span>To Watch</span>
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default MovieCard;
