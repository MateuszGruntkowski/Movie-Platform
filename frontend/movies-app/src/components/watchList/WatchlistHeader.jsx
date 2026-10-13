import { Eye, Bookmark } from "lucide-react";
import "./WatchlistHeader.css";

const WatchlistHeader = ({ toWatchCount, watchedCount }) => (
    <header className="wl-header">
        <h1>My Movie List</h1>
        <div className="wl-stats">
            <span className="wl-stat">
                <Bookmark size={16} strokeWidth={1.5} />
                To watch <strong>{toWatchCount}</strong>
            </span>
            <span className="wl-stat">
                <Eye size={16} strokeWidth={1.5} />
                Already watched <strong>{watchedCount}</strong>
            </span>
        </div>
    </header>
);

export default WatchlistHeader;