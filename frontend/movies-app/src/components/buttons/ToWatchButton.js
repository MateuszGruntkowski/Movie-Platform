import React from "react";
import "./buttons.css";
import { Clock } from "lucide-react";

const ToWatchButton = ({ movie, isToWatch, handleWatchlistClick }) => {
  return (
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
  );
};

export default ToWatchButton;
