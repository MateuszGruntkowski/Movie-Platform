import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faVideoSlash } from "@fortawesome/free-solid-svg-icons";

const SearchMovieCard = ({ movie, onMovieClick }) => {
  const TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w300";

  const handleClick = () => {
    onMovieClick(movie);
  };

  return (
    <div className="sr-movie-card" onClick={handleClick}>
      <div className="sr-movie-poster">
        {movie.poster_path ? (
          <img
            src={`${TMDB_IMAGE_BASE_URL}${movie.poster_path}`}
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
          {movie.release_date
            ? new Date(movie.release_date).getFullYear()
            : "N/A"}
        </div>
        <div className="sr-movie-rating">
          ⭐ {movie.vote_average ? movie.vote_average.toFixed(1) : "N/A"}
        </div>
      </div>
    </div>
  );
};

export default SearchMovieCard;
