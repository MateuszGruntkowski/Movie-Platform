import { useEffect, useRef, useState } from "react";
import api from "../../api/axiosConfig";
import { useParams } from "react-router-dom";
import { useUser } from "../context/UserContext";
import { Container, Row, Col } from "react-bootstrap";
import { CheckCircle, Clock } from "lucide-react";
import ReviewForm from "./ReviewForm";
import MovieCard from "./MovieCard";
import ReviewList from "./ReviewList";
import { usePopup } from "../../hooks/usePopup";
import "./Details.css";

import React from "react";
import { movieDetailsService } from "../../Services/movieDetailsService";

const Details = ({ getMovieData, movie, reviews, setReviews, setMovie }) => {
  const revText = useRef();
  let params = useParams();
  const movieId = params.movieId;
  const [isLoading, setIsLoading] = useState(true);
  const { popup, showPopup } = usePopup();

  useEffect(() => {
    if (!movieId) return;

    setIsLoading(true);

    movieDetailsService
      .getMovieDetails(movieId)
      .then((data) => {
        setMovie(data);
        console.log("Movie details fetched:", data);
        setReviews(data.reviews || []);
      })
      .catch((error) => {
        console.error("Error fetching movie details:", error);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, [movieId]);

  useEffect(() => {
    const fetchReviews = async () => {
      try {
        const response = await api.get(`/v1/reviews/${movieId}`);
        const reviews = response.data;
        console.log("reviews:", reviews);
        setReviews(reviews || []);
      } catch (error) {
        console.error("Error fetching reviews:", error);
      }
    };

    fetchReviews();
  }, [movieId]);

  const addReview = async (e) => {
    e.preventDefault();
    const rev = revText.current;

    try {
      const response = await api.post(`/v1/reviews/create/${movieId}`, {
        reviewBody: rev.value,
      });
      const updatedReviews = [...reviews, response.data];
      rev.value = "";
      setReviews(updatedReviews);
    } catch (err) {
      console.error("Error adding review:", err);
    }
  };

  return (
    <div className="reviews-container">
      {popup.show && (
        <div className={`popup-notification ${popup.type}`}>
          {popup.message}
        </div>
      )}

      {/* Header */}
      <div className="reviews-header">
        <h1 className="reviews-title">Reviews of the film</h1>
      </div>

      <div className="reviews-content">
        {/* Movie Card*/}
        <div className="movie-section">
          <MovieCard
            movie={movie}
            isLoading={isLoading}
            showPopup={showPopup}
          />
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
          <ReviewList reviews={reviews} />
        </div>

        {/* {isLoading ? (
          <div>Loading...</div>
        ) : (
          <div className="watchlist-buttons-container">
            <button
              className={`watchlist-button watched-button ${
                isWatched(movie.tmdbId) ? "active" : ""
              }`}
              onClick={(e) => {
                e.stopPropagation();
                handleWatchlistClick(movie.tmdbId, "watched");
                console.log(movie.tmdbId);
                console.log(typeof movie.tmdbId);
              }}
              title="Mark as watched"
            >
              <CheckCircle size={20} />
              <span>Watched</span>
            </button>

            <button
              className={`watchlist-button to-watch-button ${
                isToWatch(movie.tmdbId) ? "active" : ""
              }`}
              onClick={(e) => {
                e.stopPropagation();
                handleWatchlistClick(movie.tmdbId, "toWatch");
              }}
              title="Mark as to watch"
            >
              <Clock size={20} />
              <span>To Watch</span>
            </button>
          </div>
        )} */}
      </div>
    </div>
  );
};

export default Details;
