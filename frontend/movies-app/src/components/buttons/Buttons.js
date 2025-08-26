import React from "react";
import "./buttons.css";
import { CheckCircle, Clock } from "lucide-react";

const Buttons = ({ movie, handleWatchlistClick, isWatched, isToWatch }) => {
  return (
    <div className="watchlist-buttons-container">
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
  );
};

export default Buttons;
