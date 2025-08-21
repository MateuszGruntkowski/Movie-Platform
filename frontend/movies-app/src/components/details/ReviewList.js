import React from "react";

const ReviewList = ({ reviews }) => {
  return (
    <div className="reviews-list">
      <h2 className="reviews-list-title">
        Reviews {reviews && reviews.length ? `(${reviews.length})` : ""}
      </h2>

      {reviews?.map((review, index) => (
        <div key={review.id || index} className="review-item">
          <div className="review-header">
            <div className="review-avatar">
              {review.authorUsername?.charAt(0).toUpperCase() || "?"}
            </div>
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
          </div>
          <div className="review-body">{review.body}</div>
        </div>
      ))}
    </div>
  );
};

export default ReviewList;
