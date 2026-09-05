import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faVideoSlash, faStar } from "@fortawesome/free-solid-svg-icons";

const SearchResultItem = ({ movie, onClick }) => (
    <div className="search-result-item" onClick={() => onClick(movie)}>
        <div className="result-poster">
            {movie.posterPath ? (
                <img
                    src={movie.posterPath}
                    alt={movie.title}
                    onError={(e) => {
                        e.target.src = "https://via.placeholder.com/92x138/cccccc/666666?text=No+Image";
                    }}
                />
            ) : (
                <div className="no-poster">
                    <FontAwesomeIcon icon={faVideoSlash} />
                </div>
            )}
        </div>
        <div className="result-info">
            <h6 className="result-title">{movie.title}</h6>
            <p className="result-year">
                {movie.releaseDate ? new Date(movie.releaseDate).getFullYear() : "N/A"}
            </p>
            <div className="result-rating">
                <FontAwesomeIcon icon={faStar} /> {movie.voteAverage ? movie.voteAverage.toFixed(1) : "N/A"}
            </div>
        </div>
    </div>
);

export default SearchResultItem;