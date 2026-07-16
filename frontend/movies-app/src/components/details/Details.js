import { useEffect, useRef, useState } from "react";
import api from "../../api/axiosConfig";
import { useParams } from "react-router-dom";
import { useUser } from "../context/UserContext";
import ReviewForm from "./ReviewForm";
import MovieCard from "./MovieCard";
import ReviewList from "./ReviewList";
import BackdropGallery from "./BackdropGallery";
import { usePopup } from "../../hooks/usePopup";
import "./Details.css";

import React from "react";
import { movieDetailsService } from "../../Services/movieDetailsService";

const Details = ({ movie, reviews, setReviews, setMovie }) => {
  const revText = useRef();
  const params = useParams();
  const movieId = params.movieId;
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const { popup, showPopup } = usePopup();

  // Movie details (reviews come bundled with this response — no need for a second fetch)
  useEffect(() => {
    if (!movieId) return;

    setIsLoading(true);
    setError(null);

    movieDetailsService
        .getMovieDetails(movieId)
        .then((data) => {
          setMovie(data);
          setReviews(data.reviews || []);
        })
        .catch((err) => {
          console.error("Error fetching movie details:", err);
          setError("Nie udało się załadować szczegółów filmu.");
        })
        .finally(() => {
          setIsLoading(false);
        });
  }, [movieId, setMovie, setReviews]);

  const addReview = async (e) => {
    e.preventDefault();
    const rev = revText.current;
    if (!rev.value.trim()) return;

    try {
      const response = await api.post(`/v1/reviews/create/${movieId}`, {
        reviewBody: rev.value,
      });
      setReviews((prev) => [...prev, response.data]);
      rev.value = "";
      showPopup?.("Review added!", "success");
    } catch (err) {
      console.error("Error adding review:", err);
      showPopup?.("Could not add review.", "error");
    }
  };

  if (error) {
    return (
        <div className="reviews-container">
          <p className="details-error">{error}</p>
        </div>
    );
  }

  return (
      <div className="reviews-container">
        {popup.show && (
            <div className={`popup-notification ${popup.type}`}>
              {popup.message}
            </div>
        )}

        {/* Backdrop hero */}
        {movie?.backdropPath && (
            <div
                className="details-hero"
                style={{ backgroundImage: `url(${movie.backdropPath})` }}
            >
              <div className="details-hero-overlay">
                <h1 className="reviews-title">{movie.title || "Szczegóły filmu"}</h1>
              </div>
            </div>
        )}

        {!movie?.backdropPath && (
            <div className="reviews-header">
              <h1 className="reviews-title">Reviews of the film</h1>
            </div>
        )}

        <div className="reviews-content">
          <div className="movie-section">
            <MovieCard movie={movie} isLoading={isLoading} showPopup={showPopup} />
          </div>

          <div className="reviews-section">
            {movie?.backdrops?.length > 1 && (
                <BackdropGallery backdrops={movie.backdrops} title={movie.title} />
            )}

            {movie?.overview && (
                <div className="movie-synopsis">
                  <h2 className="movie-synopsis-title">Overview</h2>
                  <p className="movie-synopsis-text">{movie.overview}</p>
                </div>
            )}

            <ReviewForm
                handleSubmit={addReview}
                revText={revText}
                labelText="Write your review:"
            />

            <ReviewList reviews={reviews} />
          </div>
        </div>
      </div>
  );
};

export default Details;