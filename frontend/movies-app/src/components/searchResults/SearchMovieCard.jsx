import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faVideoSlash } from "@fortawesome/free-solid-svg-icons";
import { Star, Info } from "lucide-react";
import "./SearchMovieCard.css";

const SearchMovieCard = ({ movie, onMovieClick }) => {
    const handleClick = () => {
        onMovieClick(movie);
    };

    return (
        <div className="sr-movie-card" onClick={handleClick}>
            <div className="sr-movie-poster">
                {movie.posterPath ? (
                    <img
                        src={movie.posterPath}
                        alt={movie.title}
                        onError={(e) => {
                            e.target.src =
                                "https://via.placeholder.com/300x450/2A2D3A/FFD700?text=No+Poster";
                        }}
                    />
                ) : (
                    <div className="sr-no-poster">
                        <FontAwesomeIcon icon={faVideoSlash} size="3x" />
                    </div>
                )}

                {movie.voteAverage != null && (
                    <div className="sr-movie-rating">
                        <Star size={14} fill="currentColor" />
                        <span>{movie.voteAverage.toFixed(1)}</span>
                    </div>
                )}

                <div className="sr-movie-overlay">
                    <div className="sr-movie-actions">
                        <button
                            className="sr-action-btn sr-details-btn"
                            onClick={(e) => {
                                e.stopPropagation();
                                onMovieClick(movie);
                            }}
                            title="Zobacz więcej"
                        >
                            <Info size={20} />
                            <span>See more</span>
                        </button>
                    </div>
                </div>
            </div>

            <div className="sr-movie-info">
                <h3 className="sr-movie-title" title={movie.title}>
                    {movie.title}
                </h3>
                <p className="sr-movie-year">
                    {movie.releaseDate
                        ? new Date(movie.releaseDate).getFullYear()
                        : "N/A"}
                </p>
            </div>
        </div>
    );
};

export default SearchMovieCard;