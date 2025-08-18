import React, { useState, useEffect } from "react";
import { Eye, EyeOff, Clock } from "lucide-react";
import api from "../../api/axiosConfig";
import MovieCard from "./MovieCard";
import "./WatchList.css";
import { useUser } from "../context/UserContext";
import { Navigate } from "react-router-dom";

const WatchList = () => {
  const { user, loading } = useUser();

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

  const markAsWatched = async (movieId) => {
    try {
      await api.patch(`/v1/users/watchlist/watched/${movieId}`);

      const movie = moviesToWatch.find((m) => m.id === movieId);
      if (movie) {
        setMoviesToWatch((prev) => prev.filter((m) => m.id !== movieId));
        setMoviesWatched((prev) => [...prev, movie]);
      }
    } catch (error) {
      console.error("Error marking as watched:", error);
    }
  };

  const markAsToWatch = async (movieId) => {
    try {
      await api.patch(`/v1/users/watchlist/toWatch/${movieId}`);

      const movie = moviesWatched.find((m) => m.id === movieId);
      if (movie) {
        setMoviesWatched((prev) => prev.filter((m) => m.id !== movieId));
        setMoviesToWatch((prev) => [...prev, movie]);
      }
    } catch (error) {
      console.error("Error marking as to watch:", error);
    }
  };

  const removeFromToWatch = async (movieId) => {
    try {
      await api.delete(`/v1/users/watchlist/toWatch/${movieId}`);
    } catch (error) {
      console.error("Error removing movie from to watch list:", error);
    }
    setMoviesToWatch((prev) => prev.filter((m) => m.id !== movieId));
  };

  const removeFromWatched = async (movieId) => {
    try {
      await api.delete(`/v1/users/watchlist/watched/${movieId}`);
    } catch (error) {
      console.error("Error removing movie from watched list:", error);
    }
    setMoviesWatched((prev) => prev.filter((m) => m.id !== movieId));
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

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
        {/* Sekcja filmów do obejrzenia */}
        <section className="wl-movies-section">
          <div className="wl-section-header">
            <h2>
              <Clock size={24} />
              To watch
            </h2>
            <span className="wl-count">{moviesToWatch.length}</span>
          </div>
          <div className="wl-movies-grid">
            {moviesToWatch.length > 0 ? (
              moviesToWatch.map((movie) => (
                <MovieCard
                  key={movie.id}
                  movie={movie}
                  listType="moviesToWatch"
                  onMarkAsWatched={markAsWatched}
                  onMarkAsToWatch={markAsToWatch}
                  onRemove={removeFromToWatch}
                />
              ))
            ) : (
              <div className="wl-empty-state">
                <EyeOff size={48} />
                <p>No videos to watch</p>
                <small>Add movies to your list!</small>
              </div>
            )}
          </div>
        </section>

        {/* Sekcja obejrzanych filmów */}
        <section className="wl-movies-section">
          <div className="wl-section-header">
            <h2>
              <Eye size={24} />
              Already watched
            </h2>
            <span className="wl-count">{moviesWatched.length}</span>
          </div>
          <div className="wl-movies-grid">
            {moviesWatched.length > 0 ? (
              moviesWatched.map((movie) => (
                <MovieCard
                  key={movie.id}
                  movie={movie}
                  listType="moviesWatched"
                  onMarkAsWatched={markAsWatched}
                  onMarkAsToWatch={markAsToWatch}
                  onRemove={removeFromWatched}
                />
              ))
            ) : (
              <div className="wl-empty-state">
                <Eye size={48} />
                <p>You haven't watched any films yet</p>
                <small>Mark films as watched!</small>
              </div>
            )}
          </div>
        </section>
      </div>
    </div>
  );
};

export default WatchList;
