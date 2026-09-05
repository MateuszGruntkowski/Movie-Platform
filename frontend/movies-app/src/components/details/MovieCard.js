import { Link } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCirclePlay, faStar } from "@fortawesome/free-solid-svg-icons";
import "./MovieCard.css";
import { useWatchlist } from "../context/WatchlistContext";
import ToWatchButton from "../buttons/ToWatchButton";
import WatchedButton from "../buttons/WatchedButton";

const getYoutubeId = (url) => {
  try {
    const u = new URL(url);
    if (u.hostname.includes("youtu.be")) return u.pathname.slice(1);
    return u.searchParams.get("v");
  } catch {
    return null;
  }
};

const formatCurrency = (amount) => {
  if (!amount) return null;
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
    notation: "compact",
    maximumFractionDigits: 1,
  }).format(amount);
};

const formatLanguage = (code) => {
  if (!code) return null;
  try {
    const displayNames = new Intl.DisplayNames(["en"], { type: "language" });
    const name = displayNames.of(code);
    return name ? name.charAt(0).toUpperCase() + name.slice(1) : code.toUpperCase();
  } catch {
    return code.toUpperCase();
  }
};

const MovieCard = ({ movie, isLoading, showPopup }) => {
  if (!movie) return null;

  const { toggleMovieStatus, isWatched, isToWatch } = useWatchlist();

  const handleWatchlistClick = async (movieId, listType) => {
    try {
      const isNowInList = await toggleMovieStatus(movieId, listType);
      const action = isNowInList ? "added" : "removed";
      const listName = listType === "watched" ? "watched" : "watch list";
      showPopup?.(
          `Movie ${action} ${isNowInList ? "to" : "from"} ${listName}!`,
          listType
      );
    } catch (error) {
      if (error.message === "NOT_AUTHENTICATED") {
        showPopup?.("Zaloguj się, aby dodać do listy!", "login");
      } else {
        console.error("Error toggling movie status:", error);
        showPopup?.("Something went wrong!", "error");
      }
    }
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

  const renderGenres = (genres) => {
    if (!genres || !Array.isArray(genres) || genres.length === 0) return null;

    return (
        <div className="movie-genres">
          {genres.map((genre, index) => (
              <span key={genre.id ?? genre.name ?? index} className="genre-tag">
          {genre.name || genre}
        </span>
          ))}
        </div>
    );
  };

  const youtubeId = movie.trailerUrl ? getYoutubeId(movie.trailerUrl) : null;
  const budgetLabel = formatCurrency(movie.budget);
  const revenueLabel = formatCurrency(movie.revenue);
  const languageLabel = formatLanguage(movie.originalLanguage);

  return (
      <div className="movie-poster-container">
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

          <div className="movie-meta-row">
            {movie.releaseDate && (
                <span className="movie-year">{formatReleaseDate(movie.releaseDate)}</span>
            )}
            {movie.adult && <span className="adult-badge">18+</span>}
          </div>

          {movie.tagline && (
              <p className="movie-tagline">&ldquo;{movie.tagline}&rdquo;</p>
          )}

          <div className="movie-details">
            {movie.voteAverage != null && (
                <div className="movie-rating">
              <span className="rating-value">
                <FontAwesomeIcon icon={faStar} /> {movie.voteAverage.toFixed(1)}
              </span>
                  {movie.voteCount != null && (
                      <span className="vote-count">({movie.voteCount} votes)</span>
                  )}
                </div>
            )}

            {movie.runtime && (
                <div className="movie-runtime">{formatRuntime(movie.runtime)}</div>
            )}
          </div>

          {(budgetLabel || revenueLabel || languageLabel) && (
              <div className="movie-facts-grid">
                {languageLabel && (
                    <div className="movie-fact">
                      <span className="movie-fact-label">Language</span>
                      <span className="movie-fact-value">{languageLabel}</span>
                    </div>
                )}
                {budgetLabel && (
                    <div className="movie-fact">
                      <span className="movie-fact-label">Budget</span>
                      <span className="movie-fact-value">{budgetLabel}</span>
                    </div>
                )}
                {revenueLabel && (
                    <div className="movie-fact">
                      <span className="movie-fact-label">Revenue</span>
                      <span className="movie-fact-value">{revenueLabel}</span>
                    </div>
                )}
              </div>
          )}

          {renderGenres(movie.genres)}

          <div className="movie-actions">
            {youtubeId && (
                <Link to={`/Trailer/${youtubeId}`} className="play-button-icon-container">
                  <FontAwesomeIcon className="play-button-icon" icon={faCirclePlay} />
                </Link>
            )}

            {movie.imdbId && (
              <a
                href={`https://www.imdb.com/title/${movie.imdbId}/`}
                target="_blank"
                rel="noopener noreferrer"
                className="imdb-link"
              >
                IMDb
              </a>
              )}
          </div>

          {isLoading ? (
              <div>Loading...</div>
          ) : (
              <div className="watchlist-buttons-container">
                <WatchedButton
                    movie={movie}
                    handleWatchlistClick={handleWatchlistClick}
                    isWatched={isWatched}
                />
                <ToWatchButton
                    movie={movie}
                    handleWatchlistClick={handleWatchlistClick}
                    isToWatch={isToWatch}
                />
              </div>
          )}
        </div>
      </div>
  );
};

export default MovieCard;