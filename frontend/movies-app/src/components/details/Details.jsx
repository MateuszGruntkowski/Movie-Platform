import { useRef } from "react";
import { useParams } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import ReviewForm from "./ReviewForm";
import MovieCard from "./MovieCard";
import ReviewList from "./ReviewList";
import BackdropGallery from "./BackdropGallery";
import MovieRating from "./MovieRating";
import DetailsHero from "./DetailsHero.jsx";
import Popup from "../common/Popup.jsx";
import { usePopup } from "../../hooks/usePopup";
import { useMovieDetails } from "./hooks/useMovieDetails.js";
import { useReviews } from "./hooks/useReviews.js";
import "./Details.css";

const Details = () => {
  const revText = useRef();
  const { movieId } = useParams();
  const { user } = useAuth();
  const { popup, showPopup } = usePopup();

  const { movie, isLoading, error } = useMovieDetails(movieId);
  const {
    reviews,
    totalReviews,
    hasMore,
    isLoading: isLoadingReviews,
    isLoadingMore,
    loadMore,
    addReview,
    deleteReview,
  } = useReviews(movieId, showPopup);

  const handleSubmit = (e) => {
    e.preventDefault();
    addReview(revText.current.value);
    revText.current.value = "";
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
        <Popup popup={popup} />

        <DetailsHero movie={movie} />

        <div className="reviews-content">
          <div className="movie-section">
            <MovieCard movie={movie} isLoading={isLoading} showPopup={showPopup} />
            <MovieRating movieId={movieId} showPopup={showPopup} />
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
                handleSubmit={handleSubmit}
                revText={revText}
                labelText="Write your review:"
            />

            <ReviewList
                reviews={reviews}
                totalReviews={totalReviews}
                isLoading={isLoadingReviews}
                hasMore={hasMore}
                isLoadingMore={isLoadingMore}
                onLoadMore={loadMore}
                currentUsername={user?.username}
                onDelete={deleteReview}
            />
          </div>
        </div>
      </div>
  );
};

export default Details;