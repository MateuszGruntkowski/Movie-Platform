import React, { useState, useEffect, useCallback } from "react";
import { Eye, Clock } from "lucide-react";
import "./WatchList.css";
import { useAuth } from "../context/AuthContext";
import { useWatchlist } from "../context/WatchlistContext";
import { usePopup } from "../../hooks/usePopup";
import { Navigate } from "react-router-dom";
import WatchlistSection from "./WatchlistSection";
import { watchlistService } from "../../Services/watchlistService";

const PAGE_SIZE = 10;

const emptyListState = {
    movies: [],
    total: 0,
    page: 0,
    isLast: true,
    isLoading: false,
};

const FETCHERS = {
    moviesToWatch: watchlistService.getMoviesToWatch,
    moviesWatched: watchlistService.getMoviesWatched,
};

const WatchList = () => {
    const { user, loading } = useAuth();
    const { toggleMovieStatus } = useWatchlist();
    const { popup, showPopup } = usePopup();

    const [toWatch, setToWatch] = useState(emptyListState);
    const [watched, setWatched] = useState(emptyListState);

    const getState = (listType) =>
        listType === "moviesToWatch" ? [toWatch, setToWatch] : [watched, setWatched];

    const loadInitialPage = useCallback(async (listType) => {
        const [, setState] = getState(listType);
        setState((prev) => ({ ...prev, isLoading: true }));
        try {
            const data = await FETCHERS[listType]({ page: 0, size: PAGE_SIZE });
            setState({
                movies: data.content,
                total: data.totalElements,
                page: 0,
                isLast: data.last,
                isLoading: false,
            });
        } catch (error) {
            console.error(`Error loading ${listType}:`, error);
            setState((prev) => ({ ...prev, isLoading: false }));
        }
    }, []);

    useEffect(() => {
        if (user) {
            loadInitialPage("moviesToWatch");
            loadInitialPage("moviesWatched");
        }
    }, [user, loadInitialPage]);

    const handleLoadMore = async (listType) => {
        const [state, setState] = getState(listType);
        const nextPage = state.page + 1;
        setState((prev) => ({ ...prev, isLoading: true }));
        try {
            const data = await FETCHERS[listType]({ page: nextPage, size: PAGE_SIZE });
            setState((prev) => ({
                movies: [...prev.movies, ...data.content],
                total: data.totalElements,
                page: nextPage,
                isLast: data.last,
                isLoading: false,
            }));
        } catch (error) {
            console.error(`Error loading more ${listType}:`, error);
            setState((prev) => ({ ...prev, isLoading: false }));
        }
    };

    // Optimistyczne przeniesienie karty między listami - UI reaguje natychmiast,
    // request do API leci w tle. W razie błędu cofamy zmianę.
    const handleToggle = async (movie, sourceListType, targetListType) => {
        const [, setSource] = getState(sourceListType);
        const [, setTarget] = getState(targetListType);

        setSource((prev) => ({
            ...prev,
            movies: prev.movies.filter((m) => m.tmdbId !== movie.tmdbId),
            total: Math.max(0, prev.total - 1),
        }));
        setTarget((prev) => ({
            ...prev,
            movies: [movie, ...prev.movies],
            total: prev.total + 1,
        }));

        try {
            await toggleMovieStatus(
                movie.tmdbId,
                targetListType === "moviesWatched" ? "watched" : "toWatch"
            );
        } catch (error) {
            console.error("Error toggling movie status:", error);
            showPopup?.("Something went wrong!", "error");
            // Rollback przy błędzie
            setSource((prev) => ({
                ...prev,
                movies: [movie, ...prev.movies],
                total: prev.total + 1,
            }));
            setTarget((prev) => ({
                ...prev,
                movies: prev.movies.filter((m) => m.tmdbId !== movie.tmdbId),
                total: Math.max(0, prev.total - 1),
            }));
        }
    };

    // Usunięcie z listy bez przenoszenia (np. przycisk X)
    const handleRemove = async (movie, sourceListType) => {
        const [, setSource] = getState(sourceListType);

        setSource((prev) => ({
            ...prev,
            movies: prev.movies.filter((m) => m.tmdbId !== movie.tmdbId),
            total: Math.max(0, prev.total - 1),
        }));

        try {
            await toggleMovieStatus(
                movie.tmdbId,
                sourceListType === "moviesWatched" ? "watched" : "toWatch"
            );
        } catch (error) {
            console.error("Error removing movie:", error);
            showPopup?.("Something went wrong!", "error");
            setSource((prev) => ({
                ...prev,
                movies: [movie, ...prev.movies],
                total: prev.total + 1,
            }));
        }
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    return (
        <div className="wl-container">
            {popup.show && (
                <div className={`popup-notification ${popup.type}`}>
                    {popup.message}
                </div>
            )}

            <header className="wl-header">
                <h1>My Movie List</h1>
                <div className="wl-stats">
                    <span className="wl-stat">
                        <Clock size={16} />
                        To watch: {toWatch.total}
                    </span>
                    <span className="wl-stat">
                        <Eye size={16} />
                        Already watched: {watched.total}
                    </span>
                </div>
            </header>

            <div className="wl-content">
                <WatchlistSection
                    title="To Watch"
                    icon={<Clock size={24} />}
                    listType="moviesToWatch"
                    state={toWatch}
                    onLoadMore={() => handleLoadMore("moviesToWatch")}
                    onMarkAsWatched={(movie) =>
                        handleToggle(movie, "moviesToWatch", "moviesWatched")
                    }
                    onRemove={(movie) => handleRemove(movie, "moviesToWatch")}
                    emptyMessage="No movies to watch"
                    emptySubMessage="Add movies to your list!"
                />

                <WatchlistSection
                    title="Already Watched"
                    icon={<Eye size={24} />}
                    listType="moviesWatched"
                    state={watched}
                    onLoadMore={() => handleLoadMore("moviesWatched")}
                    onMarkAsToWatch={(movie) =>
                        handleToggle(movie, "moviesWatched", "moviesToWatch")
                    }
                    onRemove={(movie) => handleRemove(movie, "moviesWatched")}
                    emptyMessage="You haven't watched any movies yet"
                    emptySubMessage="Mark movies as watched!"
                />
            </div>
        </div>
    );
};

export default WatchList;