import React from "react";
import { getAvatarUrl } from "../../utils/avatarUtils";

const ReviewList = ({ reviews }) => {
  return (
    <div className="reviews-list">
      <h2 className="reviews-list-title">
        Reviews {reviews && reviews.length ? `(${reviews.length})` : ""}
      </h2>

      {reviews?.map((review, index) => {
          const avatarUrl = getAvatarUrl(review.authorAvatarPath);
          return(
          <div key={review.id || index} className="review-item">
              <div className="review-header">
                  {avatarUrl ? (
                      <img src={avatarUrl} alt="avatar" className="review-avatar-img"/>
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
              </div>
              <div className="review-body">{review.body}</div>
          </div>
          );
      })}
    </div>
  );
};

export default ReviewList;
