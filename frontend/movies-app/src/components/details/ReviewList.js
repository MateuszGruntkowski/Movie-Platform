import React from "react";
import { getAvatarUrl } from "../../utils/avatarUtils";

const ReviewList = ({
                        reviews,
                        totalReviews = 0,
                        isLoading = false,
                        hasMore = false,
                        isLoadingMore = false,
                        onLoadMore,
                        currentUsername,
                        onDelete,
                    }) => {
    return (
        <div className="reviews-list">
            <h2 className="reviews-list-title">
                Reviews {totalReviews ? `(${totalReviews})` : ""}
            </h2>

            {isLoading && (!reviews || reviews.length === 0) && (
                <p className="reviews-loading">Loading reviews...</p>
            )}

            {!isLoading && reviews && reviews.length === 0 && (
                <div className="no-reviews">
                    <p>No reviews yet. Be the first!</p>
                </div>
            )}

            {reviews?.map((review, index) => {
                const avatarUrl = getAvatarUrl(review.authorAvatarPath);
                const isOwnReview =
                    currentUsername && review.authorUsername === currentUsername;

                return (
                    <div key={review.id || index} className="review-item">
                        <div className="review-header">
                            {avatarUrl ? (
                                <img src={avatarUrl} alt="avatar" className="review-avatar-img" />
                            ) : (
                                <div className="review-avatar">
                                    {review.authorUsername?.charAt(0).toUpperCase() || "?"}
                                </div>
                            )}
                            <div className="review-meta">
                                <div className="review-author">
                                    {review.authorUsername || "Anonim"}
                                </div>
                                <div className="review-date">
                                    {review.createdAt
                                        ? new Date(review.createdAt).toLocaleDateString("pl-PL")
                                        : new Date().toLocaleDateString("pl-PL")}
                                </div>
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

            {hasMore && (
                <button
                    type="button"
                    className="submit-btn"
                    onClick={onLoadMore}
                    disabled={isLoadingMore}
                    style={{ display: "block", margin: "20px auto 0" }}
                >
                    {isLoadingMore ? "Loading..." : "Load more"}
                </button>
            )}
        </div>
    );
};

export default ReviewList;