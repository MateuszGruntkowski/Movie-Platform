import React, { useState, useEffect } from "react";
import { Eye, EyeOff, Clock } from "lucide-react";
import api from "../../api/axiosConfig";
import MovieCard from "./MovieCard";
import "./WatchList.css";
import { useUser } from "../context/UserContext";
import { Navigate } from "react-router-dom";

const WatchList = () => {
  const { user, loading, updateWatchlist } = useUser();
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

  // Jedna funkcja do obsługi wszystkich akcji
  const handleWatchlistAction = async (movieId, action) => {
    const success = await updateWatchlist(movieId, action);

    if (success) {
      // Odświeżamy lokalne listy
      await fetchWatchlistData();
    }
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
              onMarkAsWatched={() =>
                handleWatchlistAction(movie.id, "add-watched")
              }
              onMarkAsToWatch={() =>
                handleWatchlistAction(movie.id, "add-toWatch")
              }
              onRemove={() =>
                handleWatchlistAction(
                  movie.id,
                  listType === "moviesToWatch"
                    ? "remove-toWatch"
                    : "remove-watched"
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
