import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import ReviewForm from "./ReviewForm";
import MovieCard from "./MovieCard";
import ReviewList from "./ReviewList";
import BackdropGallery from "./BackdropGallery";
import MovieRating from "./MovieRating";
import { usePopup } from "../../hooks/usePopup";
import "./Details.css";

import { movieDetailsService } from "../../services/movieDetailsService";
import { reviewsService } from "../../services/reviewsService";

const REVIEWS_PAGE_SIZE = 10;

const Details = () => {
  const revText = useRef();
  const params = useParams();
  const movieId = params.movieId;
  const { user } = useAuth();

  const [movie, setMovie] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const { popup, showPopup } = usePopup();

  const [reviewsPage, setReviewsPage] = useState(0);
  const [totalReviews, setTotalReviews] = useState(0);
  const [hasMoreReviews, setHasMoreReviews] = useState(false);
  const [isLoadingReviews, setIsLoadingReviews] = useState(false);
  const [isLoadingMoreReviews, setIsLoadingMoreReviews] = useState(false);

  const fetchReviews = async (page) => {
    return reviewsService.getReviewsForMovie(movieId, {
      page,
      size: REVIEWS_PAGE_SIZE,
      sort: "createdAt,desc",
    });
  };

  useEffect(() => {
    if (!movieId) return;
    let cancelled = false;

    setIsLoading(true);
    setError(null);
    setMovie(null);

    movieDetailsService
        .getMovieDetails(movieId)
        .then((data) => {
          if (cancelled) return;
          setMovie(data);
        })
        .catch((err) => {
          if (cancelled) return;
          console.error("Error fetching movie details:", err);
          setError("Failed to load movie details.");
        })
        .finally(() => {
          if (!cancelled) setIsLoading(false);
        });

    return () => {
      cancelled = true;
    };
  }, [movieId]);

  useEffect(() => {
    if (!movieId) return;
    let cancelled = false;

    setIsLoadingReviews(true);
    setReviewsPage(0);
    setReviews([]);

    fetchReviews(0)
        .then((data) => {
          if (cancelled) return;
          setReviews(data.content);
          setTotalReviews(data.totalElements);
          setHasMoreReviews(!data.last);
        })
        .catch((err) => {
          if (cancelled) return;
          console.error("Error fetching reviews:", err);
          showPopup?.("Failed to load review.", "error");
        })
        .finally(() => {
          if (!cancelled) setIsLoadingReviews(false);
        });

    return () => {
      cancelled = true;
    };
  }, [movieId]);

  const loadMoreReviews = async () => {
    const nextPage = reviewsPage + 1;
    setIsLoadingMoreReviews(true);
    try {
      const data = await fetchReviews(nextPage);
      setReviews((prev) => [...prev, ...data.content]);
      setReviewsPage(nextPage);
      setHasMoreReviews(!data.last);
    } catch (err) {
      console.error("Error loading more reviews:", err);
      showPopup?.("Could not load more reviews.", "error");
    } finally {
      setIsLoadingMoreReviews(false);
    }
  };

  const addReview = async (e) => {
    e.preventDefault();
    const rev = revText.current;
    if (!rev.value.trim()) return;

    try {
      const newReview = await reviewsService.createReview(movieId, rev.value);
      setReviews((prev) => [newReview, ...prev]);
      setTotalReviews((prev) => prev + 1);
      rev.value = "";
      showPopup?.("Review added!", "success");
    } catch (err) {
      console.error("Error adding review:", err);
      showPopup?.("Could not add review.", "error");
    }
  };

  const deleteReview = async (reviewId) => {
    try {
      await reviewsService.deleteReview(reviewId);
      setReviews((prev) => prev.filter((r) => r.id !== reviewId));
      setTotalReviews((prev) => Math.max(prev - 1, 0));
      showPopup?.("Review deleted!", "success");
    } catch (err) {
      console.error("Error deleting review:", err);
      showPopup?.("Could not delete review.", "error");
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
                handleSubmit={addReview}
                revText={revText}
                labelText="Write your review:"
            />

            <ReviewList
                reviews={reviews}
                totalReviews={totalReviews}
                isLoading={isLoadingReviews}
                hasMore={hasMoreReviews}
                isLoadingMore={isLoadingMoreReviews}
                onLoadMore={loadMoreReviews}
                currentUsername={user?.username}
                onDelete={deleteReview}
            />
          </div>
        </div>
      </div>
  );
};

export default Details;