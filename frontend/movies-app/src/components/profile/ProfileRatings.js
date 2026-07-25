import { Link } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faStar } from "@fortawesome/free-solid-svg-icons";

const ProfileRatings = ({
                            ratings,
                            isLoading = false,
                            hasMore = false,
                            isLoadingMore = false,
                            onLoadMore,
                        }) => {
    if (isLoading && (!ratings || ratings.length === 0)) {
        return <p className="profile-loading">Loading ratings...</p>;
    }

    if (!ratings || ratings.length === 0) {
        return (
            <div className="profile-empty">
                <p>You haven't rated any movies yet.</p>
            </div>
        );
    }

    return (
        <div className="profile-list">
            {ratings.map((rating) => (
                <Link
                    to={`/Details/${rating.movie.tmdbId}`}
                    className="profile-list-item"
                    key={rating.id}
                >
                    <img
                        src={
                            rating.movie.posterPath ||
                            "https://via.placeholder.com/80x120/2A2D3A/FFD700?text=No+Poster"
                        }
                        alt={rating.movie.title}
                        className="profile-list-poster"
                    />
                    <div className="profile-list-content">
                        <h4 className="profile-list-title">{rating.movie.title}</h4>
                        <span className="profile-list-rating">
                            <FontAwesomeIcon icon={faStar} className="profile-rating-star" />{" "}
                            {rating.rating}/10
                        </span>
                        <span className="profile-list-date">
                            {new Date(rating.createdAt).toLocaleDateString()}
                        </span>
                    </div>
                </Link>
            ))}

            {hasMore && (
                <button
                    type="button"
                    className="submit-btn"
                    onClick={onLoadMore}
                    disabled={isLoadingMore}
                >
                    {isLoadingMore ? "Loading..." : "Show more"}
                </button>
            )}
        </div>
    );
};

export default ProfileRatings;