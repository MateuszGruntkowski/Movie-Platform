import { useNavigate } from "react-router-dom";

const ProfileReviews = ({
                            reviews,
                            isLoading = false,
                            hasMore = false,
                            isLoadingMore = false,
                            onLoadMore,
                            isOwnProfile = false,
                            onDelete,
                        }) => {
    const navigate = useNavigate();

    if (isLoading && (!reviews || reviews.length === 0)) {
        return <p className="profile-loading">Loading reviews...</p>;
    }

    if (!reviews || reviews.length === 0) {
        return (
            <div className="profile-empty">
                <p>You haven't written any reviews yet.</p>
            </div>
        );
    }

    const handleDeleteClick = (e, reviewId) => {
        e.stopPropagation();
        onDelete(reviewId);
    };

    return (
        <div className="profile-list">
            {reviews.map((review) => (
                <div
                    className="profile-list-item"
                    key={review.id}
                    onClick={() => navigate(`/Details/${review.movie.tmdbId}`)}
                >
                    <img
                        src={
                            review.movie.posterPath ||
                            "https://via.placeholder.com/80x120/2A2D3A/FFD700?text=No+Poster"
                        }
                        alt={review.movie.title}
                        className="profile-list-poster"
                    />
                    <div className="profile-list-content">
                        <div className="profile-list-header">
                            <h4 className="profile-list-title">{review.movie.title}</h4>

                            {isOwnProfile && (
                                <button
                                    type="button"
                                    className="review-delete-btn"
                                    onClick={(e) => handleDeleteClick(e, review.id)}
                                    aria-label="Delete review"
                                >
                                    Delete
                                </button>
                            )}
                        </div>

                        <p className="profile-list-body">{review.body}</p>
                        <span className="profile-list-date">
                            {new Date(review.createdAt).toLocaleDateString()}
                        </span>
                    </div>
                </div>
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

export default ProfileReviews;