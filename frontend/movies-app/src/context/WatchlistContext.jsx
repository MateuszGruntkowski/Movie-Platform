import { createContext, useContext, useState, useEffect } from "react";
import { watchlistService } from "../services/watchlistService";
import { useAuth } from "./AuthContext";

const WatchlistContext = createContext(null);

export const useWatchlist = () => useContext(WatchlistContext);

export const WatchlistProvider = ({ children }) => {
    const { user } = useAuth();
    const [toWatchIds, setToWatchIds] = useState([]);
    const [watchedIds, setWatchedIds] = useState([]);

    useEffect(() => {
        if (!user) {
            setToWatchIds([]);
            setWatchedIds([]);
            return;
        }

        let cancelled = false;

        watchlistService.getWatchlistIds()
            .then((data) => {
                if (cancelled) return;
                setToWatchIds(data.moviesToWatchIds);
                setWatchedIds(data.moviesWatchedIds);
            })
            .catch((error) => {
                console.error("Error loading watchlist ids:", error);
            });

        return () => {
            cancelled = true;
        };
    }, [user?.username]);

    const toggleMovieStatus = async (movieId, listType) => {
        if (!user) {
            throw new Error("NOT_AUTHENTICATED");
        }

        const status = await watchlistService.toggleStatus(movieId, listType);

        setToWatchIds((prev) =>
            status.inToWatch ? [...prev, movieId] : prev.filter((id) => id !== movieId)
        );
        setWatchedIds((prev) =>
            status.inWatched ? [...prev, movieId] : prev.filter((id) => id !== movieId)
        );

        return listType === "watched" ? status.inWatched : status.inToWatch;
    };

    const isWatched = (movieId) => watchedIds.includes(movieId);
    const isToWatch = (movieId) => toWatchIds.includes(movieId);

    return (
        <WatchlistContext.Provider value={{ isWatched, isToWatch, toggleMovieStatus }}>
            {children}
        </WatchlistContext.Provider>
    );
};