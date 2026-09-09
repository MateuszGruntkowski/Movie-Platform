import { Eye, Clock } from "lucide-react";
import "./WatchlistHeader.css";

const WatchlistHeader = ({ toWatchCount, watchedCount }) => (
    <header className="wl-header">
        <h1>My Movie List</h1>
        <div className="wl-stats">
            <span className="wl-stat">
                <Clock size={16} />
                To watch: {toWatchCount}
            </span>
            <span className="wl-stat">
                <Eye size={16} />
                Already watched: {watchedCount}
            </span>
        </div>
    </header>
);

export default WatchlistHeader;