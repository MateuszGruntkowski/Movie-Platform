import React, { useState, useEffect } from "react";
import { Eye, EyeOff, Clock } from "lucide-react";
import api from "../../api/axiosConfig";
import MovieCard from "./MovieCard";
import "./WatchList.css";
import { useUser } from "../context/UserContext";
import { usePopup } from "../../hooks/usePopup";
import { Navigate } from "react-router-dom";

const WatchList = () => {
  const { user, loading, toggleMovieStatus } = useUser();
  const { popup, showPopup } = usePopup();
  const [moviesToWatch, setMoviesToWatch] = useState([]);
  const [moviesWatched, setMoviesWatched] = useState([]);

  const fetchWatchlistData = async () => {
    try {
      const response = await api.get("/v1/users/watchlist");
      setMoviesToWatch(response.data.moviesToWatch);
      setMoviesWatched(response.data.moviesWatched);
    } catch (error) {
      console.error("Error fetching watchlist data:", error);
    }
  };

  useEffect(() => {
    if (user) {
      fetchWatchlistData();
    }
  }, [user]);

  // Jedna funkcja do obsługi wszystkich akcji - używa tylko toggle endpoint
  const handleWatchlistAction = async (movieId, targetListType) => {
    await toggleMovieStatus(movieId, targetListType, showPopup);
    // Zawsze odświeżamy dane - nawet jeśli był błąd, żeby mieć aktualny stan
    await fetchWatchlistData();
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  const renderMoviesSection = (
    title,
    icon,
    movies,
    listType,
    emptyMessage,
    emptySubMessage
  ) => (
    <section className="wl-movies-section">
      <div className="wl-section-header">
        <h2>
          {icon}
          {title}
        </h2>
        <span className="wl-count">{movies.length}</span>
      </div>
      <div className="wl-movies-grid">
        {movies.length > 0 ? (
          movies.map((movie) => (
            <MovieCard
              key={movie.id}
              movie={movie}
              listType={listType}
              // Przekazujemy odpowiedni listType do toggle
              onMarkAsWatched={() =>
                handleWatchlistAction(movie.tmdbId, "watched")
              }
              onMarkAsToWatch={() =>
                handleWatchlistAction(movie.tmdbId, "toWatch")
              }
              onRemove={() =>
                handleWatchlistAction(
                  movie.tmdbId,
                  listType === "moviesToWatch" ? "toWatch" : "watched"
                )
              }
            />
          ))
        ) : (
          <div className="wl-empty-state">
            {icon}
            <p>{emptyMessage}</p>
            <small>{emptySubMessage}</small>
          </div>
        )}
      </div>
    </section>
  );

  return (
    <div className="wl-container">
      {popup.show && (
        <div className={`popup-notification ${popup.type}`}>
          {popup.message}
        </div>
      )}

      <header className="wl-header">
        <h1>My movie list</h1>
        <div className="wl-stats">
          <span className="wl-stat">
            <Clock size={16} />
            To watch: {moviesToWatch.length}
          </span>
          <span className="wl-stat">
            <Eye size={16} />
            Already Watched: {moviesWatched.length}
          </span>
        </div>
      </header>

      <div className="wl-content">
        {renderMoviesSection(
          "To watch",
          <Clock size={24} />,
          moviesToWatch,
          "moviesToWatch",
          "No videos to watch",
          "Add movies to your list!"
        )}

        {renderMoviesSection(
          "Already watched",
          <Eye size={24} />,
          moviesWatched,
          "moviesWatched",
          "You haven't watched any films yet",
          "Mark films as watched!"
        )}
      </div>
    </div>
  );
};

export default WatchList;
