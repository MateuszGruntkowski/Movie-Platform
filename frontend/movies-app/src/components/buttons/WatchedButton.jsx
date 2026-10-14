import "./buttons.css";
import { CheckCircle, Eye, Check } from "lucide-react";

const WatchedButton = ({
                           movie,
                           handleWatchlistClick,
                           isWatched,
                           variant = "default",
                       }) => {
    const active = isWatched(movie.tmdbId);

    const onClick = (e) => {
        e.stopPropagation();
        handleWatchlistClick(movie.tmdbId, "watched");
    };

    if (variant === "icon") {
        const label = active ? "Watched — click to unmark" : "Mark as watched";
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
                    <Check size={19} strokeWidth={2.25} />
                ) : (
                    <Eye size={19} strokeWidth={1.5} />
                )}
            </button>
        );
    }

    return (
        <button
            className={`watchlist-button watched-button ${active ? "active" : ""}`}
            onClick={onClick}
            title="Mark as watched"
        >
            <CheckCircle size={20} />
            <span>Watched</span>
        </button>
    );
};

export default WatchedButton;