import { Link } from "react-router-dom";
import { getAvatarUrl } from "../../utils/avatarUtils";
import ReviewsPagination from "./ReviewsPagination";
import "./ReviewList.css";

const ReviewList = ({
                        reviews = [],
                        totalReviews = 0,
                        page = 0,
                        totalPages = 0,
                        isLoading = false,
                        error = null,
                        onRetry,
                        onPageChange,
                        currentUsername,
                        onDelete,
                    }) => {
    const hasReviews = reviews.length > 0;

    return (
        <div className="reviews-list">
            <h2 className="reviews-list-title">
                Reviews {totalReviews ? `(${totalReviews})` : ""}
            </h2>

            {isLoading && !hasReviews && (
                <p className="reviews-loading">Loading reviews...</p>
            )}

            {error && !isLoading && !hasReviews && (
                <div className="no-reviews">
                    <p>
                        {error}{" "}
                        <button type="button" className="reviews-retry" onClick={onRetry}>
                            Try again
                        </button>
                    </p>
                </div>
            )}

            {!isLoading && !error && !hasReviews && (
                <div className="no-reviews">
                    <p>No reviews yet. Be the first!</p>
                </div>
            )}

            <div className="reviews-items" aria-busy={isLoading}>
                {reviews.map((review) => {
                    const avatarUrl = getAvatarUrl(review.authorAvatarPath);
                    const isOwnReview =
                        currentUsername && review.authorUsername === currentUsername;

                    return (
                        <div key={review.id} className="review-item">
                            <div className="review-header">
                                <Link
                                    to={`/profile/${review.authorUsername}`}
                                    className="review-author-link"
                                >
                                    {avatarUrl ? (
                                        <img src={avatarUrl} alt="avatar" className="review-avatar-img" />
                                    ) : (
                                        <div className="review-avatar">
                                            {review.authorUsername?.charAt(0).toUpperCase() || "?"}
                                        </div>
                                    )}
                                </Link>
                                <div className="review-meta">
                                    <Link
                                        to={`/profile/${review.authorUsername}`}
                                        className="review-author review-author-link"
                                    >
                                        {review.authorUsername || "Anonim"}
                                    </Link>
                                    {review.createdAt && (
                                        <div className="review-date">
                                            {new Date(review.createdAt).toLocaleDateString("pl-PL")}
                                        </div>
                                    )}
                                </div>

                                {isOwnReview && (
                                    <button
                                        type="button"
                                        className="review-delete-btn"
                                        onClick={() => onDelete(review.id)}
                                        aria-label="Delete review"
                                    >
                                        Delete
                                    </button>
                                )}
                            </div>
                            <div className="review-body">{review.body}</div>
                        </div>
                    );
                })}
            </div>

            {error && hasReviews && (
                <p className="reviews-note">
                    {error}{" "}
                    <button type="button" className="reviews-retry" onClick={onRetry}>
                        Try again
                    </button>
                </p>
            )}

            <ReviewsPagination
                page={page}
                totalPages={totalPages}
                onPageChange={onPageChange}
                disabled={isLoading}
            />
        </div>
    );
};

export default ReviewList;