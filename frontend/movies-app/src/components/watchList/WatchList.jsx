import { Eye, Clock } from "lucide-react";
import "./WatchList.css";
import { useAuth } from "../../context/AuthContext";
import { useWatchlist } from "../../context/WatchlistContext";
import { usePopup } from "../../hooks/usePopup";
import { useWatchlistList } from "./hooks/useWatchlistList";
import { Navigate } from "react-router-dom";
import WatchlistSection from "./WatchlistSection";
import WatchlistHeader from "./WatchlistHeader";
import WatchlistPopup from "./WatchlistPopup";
import { watchlistService } from "../../services/watchlistService";
import "./WatchList.css";

const WatchList = () => {
    const { user, loading } = useAuth();
    const { toggleMovieStatus } = useWatchlist();
    const { popup, showPopup } = usePopup();

    const toWatch = useWatchlistList(watchlistService.getMoviesToWatch, !!user);
    const watched = useWatchlistList(watchlistService.getMoviesWatched, !!user);

    // Optimistic card move between lists – the UI updates immediately,
    // while the API request runs in the background. We revert the change in case of an error.
    const handleToggle = async (movie, source, target, targetStatus) => {
        source.removeMovie(movie.tmdbId);
        target.addMovie(movie);

        try {
            await toggleMovieStatus(movie.tmdbId, targetStatus);
        } catch (error) {
            console.error("Error toggling movie status:", error);
            showPopup?.("Something went wrong!", "error");
            source.addMovie(movie);
            target.removeMovie(movie.tmdbId);
        }
    };

    // removal from the list without transfer (e.g., X button)
    const handleRemove = async (movie, source, sourceStatus) => {
        source.removeMovie(movie.tmdbId);

        try {
            await toggleMovieStatus(movie.tmdbId, sourceStatus);
        } catch (error) {
            console.error("Error removing movie:", error);
            showPopup?.("Something went wrong!", "error");
            source.addMovie(movie);
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
            <WatchlistPopup popup={popup} />

            <WatchlistHeader
                toWatchCount={toWatch.state.total}
                watchedCount={watched.state.total}
            />

            <div className="wl-content">
                <WatchlistSection
                    title="To Watch"
                    icon={<Clock size={24} />}
                    listType="moviesToWatch"
                    state={toWatch.state}
                    onLoadMore={toWatch.loadMore}
                    onMarkAsWatched={(movie) => handleToggle(movie, toWatch, watched, "watched")}
                    onRemove={(movie) => handleRemove(movie, toWatch, "toWatch")}
                    emptyMessage="No movies to watch"
                    emptySubMessage="Add movies to your list!"
                />

                <WatchlistSection
                    title="Already Watched"
                    icon={<Eye size={24} />}
                    listType="moviesWatched"
                    state={watched.state}
                    onLoadMore={watched.loadMore}
                    onMarkAsToWatch={(movie) => handleToggle(movie, watched, toWatch, "toWatch")}
                    onRemove={(movie) => handleRemove(movie, watched, "watched")}
                    emptyMessage="You haven't watched any movies yet"
                    emptySubMessage="Mark movies as watched!"
                />
            </div>
        </div>
    );
};

export default WatchList;