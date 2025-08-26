import React from "react";
import "./buttons.css";
import { CheckCircle } from "lucide-react";

const WatchedButton = ({ movie, handleWatchlistClick, isWatched }) => {
  return (
    <button
      className={`watchlist-button watched-button ${
        isWatched(movie.tmdbId) ? "active" : ""
      }`}
      onClick={(e) => {
        e.stopPropagation();
        handleWatchlistClick(movie.tmdbId, "watched");
      }}
      title="Mark as watched"
    >
      <CheckCircle size={20} />
      <span>Watched</span>
    </button>
  );
};

export default WatchedButton;
