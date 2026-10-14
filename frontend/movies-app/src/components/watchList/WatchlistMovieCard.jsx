import { Bookmark, Eye, X, Info, Star } from "lucide-react";
import { useNavigate } from "react-router-dom";
import "./WatchlistMovieCard.css";

// Action icons use the same lucide icons, size and stroke as the icon buttons on the Details page.
const ICON_PROPS = { size: 19, strokeWidth: 1.5 };

const WatchlistMovieCard = ({
                                movie,
                                listType,
                                onMarkAsWatched,
                                onMarkAsToWatch,
                                onRemove,
                            }) => {
    const navigate = useNavigate();

    return (
        <div className="wl-movie-card">
            <div className="wl-movie-poster">
                <img src={movie.posterPath} alt={movie.title} />

                {movie.voteAverage != null && (
                    <div className="wl-movie-rating">
                        <Star size={14} fill="currentColor" />
                        <span>{movie.voteAverage.toFixed(1)}</span>
                    </div>
                )}

                <div className="wl-movie-overlay">
                    <div className="wl-movie-actions">
                        {listType === "moviesToWatch" ? (
                            <button
                                className="wl-action-btn wl-watch-btn"
                                onClick={() => onMarkAsWatched(movie)}
                                title="Mark as watched"
                                aria-label="Mark as watched"
                            >
                                <Eye {...ICON_PROPS} />
                            </button>
                        ) : (
                            <button
                                className="wl-action-btn wl-unwatch-btn"
                                onClick={() => onMarkAsToWatch(movie)}
                                title="Move to watchlist"
                                aria-label="Move to watchlist"
                            >
                                <Bookmark {...ICON_PROPS} />
                            </button>
                        )}

                        <button
                            className="wl-action-btn wl-remove-btn"
                            onClick={() => onRemove(movie)}
                            title="Remove from list"
                            aria-label="Remove from list"
                        >
                            <X {...ICON_PROPS} />
                        </button>

                        <button
                            className="wl-action-btn wl-details-btn"
                            onClick={() => navigate(`/Details/${movie.tmdbId}`)}
                            title="Go to details"
                            aria-label="Go to details"
                        >
                            <Info {...ICON_PROPS} />
                        </button>
                    </div>
                </div>
            </div>

            <div className="wl-movie-info">
                <h3 className="wl-movie-title">{movie.title}</h3>
                <p className="wl-movie-year">
                    {movie.releaseDate
                        ? new Date(movie.releaseDate).getFullYear()
                        : "N/A"}
                </p>
                <div className="wl-movie-genres">
                    {movie.genres?.slice(0, 3).map((genre) => (
                        <span key={genre.id ?? genre.name} className="wl-genre-tag">
                            {genre.name}
                        </span>
                    ))}
                    {movie.genres?.length > 3 && (
                        <span className="wl-genre-more">
                            +{movie.genres.length - 3} more
                        </span>
                    )}
                </div>
            </div>
        </div>
    );
};

export default WatchlistMovieCard;