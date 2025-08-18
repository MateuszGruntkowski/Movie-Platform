import { useEffect, useRef } from "react";
import api from "../../api/axiosConfig";
import { useParams } from "react-router-dom";
import { Container, Row, Col } from "react-bootstrap";
import ReviewForm from "../reviewForm/ReviewForm";
import "./Reviews.css";

import React from "react";

const Reviews = ({ getMovieData, movie, reviews, setReviews }) => {
  const revText = useRef();
  let params = useParams();
  const movieId = params.movieId;

  useEffect(() => {
    getMovieData(movieId);
  }, []);

  const addReview = async (e) => {
    e.preventDefault();
    const rev = revText.current;

    try {
      const response = await api.post("/v1/reviews", {
        reviewBody: rev.value,
        imdbId: movieId,
      });

      const updatedReviews = [...reviews, response.data];
      console.log("Updated reviews:", updatedReviews);
      console.log("Review added:", response.data);
      rev.value = "";
      setReviews(updatedReviews);
    } catch (err) {
      console.error("Error adding review:", err);
    }
  };

  return (
    <div className="reviews-container">
      {/* Header */}
      <div className="reviews-header">
        <h1 className="reviews-title">Reviews of the film</h1>
      </div>

      <div className="reviews-content">
        {/* Movie Poster */}
        <div className="movie-section">
          <div className="movie-poster-container">
            <img
              src={
                movie?.poster ||
                "https://via.placeholder.com/300x450/2A2D3A/FFD700?text=Brak+Plakatu"
              }
              alt={movie?.title || "Movie poster"}
              className="movie-poster"
            />
            {movie && (
              <div className="movie-info">
                <h3 className="movie-title">{movie.title}</h3>
              </div>
            )}
          </div>
        </div>

        {/* Reviews Section */}
        <div className="reviews-section">
          {/* Review Form */}
          <ReviewForm
            handleSubmit={addReview}
            revText={revText}
            labelText="Write your review:"
          />

          {/* Reviews List */}
          <div className="reviews-list">
            <h2 className="reviews-list-title">
              Reviews {reviews && reviews.length ? `(${reviews.length})` : ""}
            </h2>

            {reviews?.map((review, index) => (
              <div key={review.id || index} className="review-item">
                <div className="review-header">
                  <div className="review-avatar">
                    {review.authorUsername.charAt(0).toUpperCase()}
                  </div>
                  <div className="review-meta">
                    <div className="review-author">
                      {review.authorUsername || "Anonim"}
                    </div>
                    <div className="review-date">
                      {review.createdAt ||
                        new Date().toLocaleDateString("pl-PL")}
                    </div>
                  </div>
                </div>
                <div className="review-body">{review.body}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Reviews;
