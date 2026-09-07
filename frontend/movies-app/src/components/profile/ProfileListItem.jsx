import { Link } from "react-router-dom";

const DEFAULT_POSTER = "https://via.placeholder.com/80x120/2A2D3A/FFD700?text=No+Poster";

const ProfileListItem = ({ tmdbId, posterPath, title, children }) => (
    <Link to={`/Details/${tmdbId}`} className="profile-list-item">
        <img
            src={posterPath || DEFAULT_POSTER}
            alt={title}
            className="profile-list-poster"
        />
        <div className="profile-list-content">{children}</div>
    </Link>
);

export default ProfileListItem;