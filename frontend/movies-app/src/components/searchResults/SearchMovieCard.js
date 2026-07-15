import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faVideoSlash } from "@fortawesome/free-solid-svg-icons";

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
            </div>
            <div className="sr-movie-info">
                <h3 className="sr-movie-title" title={movie.title}>
                    {movie.title}
                </h3>
                <div className="sr-movie-year">
                    {movie.releaseDate
                        ? new Date(movie.releaseDate).getFullYear()
                        : "N/A"}
                </div>
                <div className="sr-movie-rating">
                    ⭐ {movie.voteAverage ? movie.voteAverage.toFixed(1) : "N/A"}
                </div>
            </div>
        </div>
    );
};

export default SearchMovieCard;