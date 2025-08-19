import React, { useState, useEffect } from "react";
import { Eye, EyeOff, Clock } from "lucide-react";
import api from "../../api/axiosConfig";
import MovieCard from "./MovieCard";
import "./WatchList.css";
import { useUser } from "../context/UserContext";
import { Navigate } from "react-router-dom";

const WatchList = () => {
  const { user, loading, moveToWatched, moveToWatch, removeFromList } =
    useUser();

  const [moviesToWatch, setMoviesToWatch] = useState([]);
  const [moviesWatched, setMoviesWatched] = useState([]);

  const getWatchListData = async () => {
    try {
      const response = await api.get("/v1/users/watchlist");
      setMoviesToWatch(response.data.moviesToWatch);
      setMoviesWatched(response.data.moviesWatched);
    } catch (error) {
      console.error("Error fetching watchlist data:", error);
    }
  };

  useEffect(() => {
    getWatchListData();
  }, []);

  // Uniwersalna funkcja do przenoszenia filmów między listami
  const handleMoveMovie = async (movieId, fromList, toList, moveFunction) => {
    const success = await moveFunction(movieId);
    if (success) {
      const movie = fromList.find((m) => m.id === movieId);
      if (movie) {
        if (fromList === moviesToWatch) {
          setMoviesToWatch((prev) => prev.filter((m) => m.id !== movieId));
          setMoviesWatched((prev) => [...prev, movie]);
        } else {
          setMoviesWatched((prev) => prev.filter((m) => m.id !== movieId));
          setMoviesToWatch((prev) => [...prev, movie]);
        }
      }
    }
  };

  // Uniwersalna funkcja do usuwania filmów z list
  const handleRemoveMovie = async (movieId, listType, setterFunction) => {
    const success = await removeFromList(movieId, listType);
    if (success) {
      setterFunction((prev) => prev.filter((m) => m.id !== movieId));
    }
  };

  const markAsWatched = (movieId) =>
    handleMoveMovie(movieId, moviesToWatch, moviesWatched, moveToWatched);

  const markAsToWatch = (movieId) =>
    handleMoveMovie(movieId, moviesWatched, moviesToWatch, moveToWatch);

  const removeFromToWatch = (movieId) =>
    handleRemoveMovie(movieId, "toWatch", setMoviesToWatch);

  const removeFromWatched = (movieId) =>
    handleRemoveMovie(movieId, "watched", setMoviesWatched);

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
    count,
    listType,
    onMarkAs,
    onRemove,
    emptyMessage,
    emptySubMessage
  ) => (
    <section className="wl-movies-section">
      <div className="wl-section-header">
        <h2>
          {icon}
          {title}
        </h2>
        <span className="wl-count">{count}</span>
      </div>
      <div className="wl-movies-grid">
        {movies.length > 0 ? (
          movies.map((movie) => (
            <MovieCard
              key={movie.id}
              movie={movie}
              listType={listType}
              onMarkAsWatched={markAsWatched}
              onMarkAsToWatch={markAsToWatch}
              onRemove={onRemove}
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
          moviesToWatch.length,
          "moviesToWatch",
          markAsWatched,
          removeFromToWatch,
          "No videos to watch",
          "Add movies to your list!"
        )}

        {renderMoviesSection(
          "Already watched",
          <Eye size={24} />,
          moviesWatched,
          moviesWatched.length,
          "moviesWatched",
          markAsToWatch,
          removeFromWatched,
          "You haven't watched any films yet",
          "Mark films as watched!"
        )}
      </div>
    </div>
  );
};

export default WatchList;
