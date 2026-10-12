import "./buttons.css";
import { Bookmark, BookmarkCheck, Clock } from "lucide-react";

const ToWatchButton = ({
                           movie,
                           isToWatch,
                           handleWatchlistClick,
                           variant = "default",
                       }) => {
    const active = isToWatch(movie.tmdbId);

    const onClick = (e) => {
        e.stopPropagation();
        handleWatchlistClick(movie.tmdbId, "toWatch");
    };

    if (variant === "icon") {
        const label = active ? "On your watchlist — click to remove" : "Add to watchlist";
        return (
            <button
                type="button"
                className={`util-button ${active ? "is-active" : ""}`}
                onClick={onClick}
                aria-pressed={active}
                aria-label={label}
                title={label}
            >
                {active ? (
                    <BookmarkCheck size={19} strokeWidth={1.75} fill="currentColor" fillOpacity={0.18} />
                ) : (
                    <Bookmark size={19} strokeWidth={1.5} />
                )}
            </button>
        );
    }

    return (
        <button
            className={`watchlist-button to-watch-button ${active ? "active" : ""}`}
            onClick={onClick}
            title="Mark as to watch"
        >
            <Clock size={20} />
            <span>To Watch</span>
        </button>
    );
};

export default ToWatchButton;