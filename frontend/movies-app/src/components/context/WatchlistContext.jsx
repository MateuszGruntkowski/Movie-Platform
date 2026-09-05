import { createContext, useContext, useState, useEffect } from "react";
import { watchlistService } from "../../Services/watchlistService";
import { useAuth } from "./AuthContext";

const WatchlistContext = createContext(null);

export const useWatchlist = () => useContext(WatchlistContext);

export const WatchlistProvider = ({ children }) => {
    const { user } = useAuth();
    const [toWatchIds, setToWatchIds] = useState([]);
    const [watchedIds, setWatchedIds] = useState([]);

    useEffect(() => {
        setToWatchIds(user?.moviesToWatchIds ?? []);
        setWatchedIds(user?.moviesWatchedIds ?? []);
    }, [user]);

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