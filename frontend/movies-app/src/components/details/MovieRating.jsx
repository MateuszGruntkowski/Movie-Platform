import { useEffect, useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faStar } from "@fortawesome/free-solid-svg-icons";
import { movieRatingService } from "../../services/movieRatingService";
import { useAuth } from "../../context/AuthContext";
import "./MovieRating.css";

const RATING_SCALE = [...Array(10)].map((_, i) => i + 1);

const MovieRating = ({ movieId, showPopup }) => {
    const { user } = useAuth();
    const [myRating, setMyRating] = useState(null);
    const [hoverRating, setHoverRating] = useState(0);
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        if (!movieId || !user) {
            setIsLoading(false);
            return;
        }

        setIsLoading(true);
        movieRatingService
            .getMyRating(movieId)
            .then((data) => setMyRating(data ? data.rating : null))
            .catch((err) => console.error("Error fetching user rating:", err))
            .finally(() => setIsLoading(false));
    }, [movieId, user]);

    const submitRating = async (rating) => {
        setIsSubmitting(true);
        try {
            const response = await movieRatingService.updateRating(movieId, rating);
            setMyRating(response.rating);
            showPopup?.("Rating saved!", "success");
        } catch (err) {
            console.error("Error submitting rating:", err);
            showPopup?.("Could not save rating.", "error");
        } finally {
            setIsSubmitting(false);
        }
    };

    if (!user) {
        return (
            <div className="movie-rating-widget">
                <p className="rating-note">Log in to rate this movie</p>
            </div>
        );
    }

    if (isLoading) {
        return (
            <div className="movie-rating-widget">
                <p className="rating-note">Loading rating…</p>
            </div>
        );
    }

    const displayedValue = hoverRating || myRating || 0;

    return (
        <div className="movie-rating-widget">
            <h2 className="movie-rating-title">Your rating</h2>

            <div className="rating-stars-picker">
                {RATING_SCALE.map((value) => {
                    const isFilled = value <= displayedValue;
                    return (
                        <button
                            key={value}
                            type="button"
                            className="rating-star-button"
                            disabled={isSubmitting}
                            onMouseEnter={() => setHoverRating(value)}
                            onMouseLeave={() => setHoverRating(0)}
                            onClick={() => submitRating(value)}
                            aria-label={`Rate ${value} out of 10`}
                            aria-pressed={value === myRating}
                        >
                            <FontAwesomeIcon
                                icon={faStar}
                                className={isFilled ? "star-filled" : "star-empty"}
                            />
                        </button>
                    );
                })}
            </div>

            <span className="rating-value-label" aria-live="polite">
                {displayedValue > 0 ? `${displayedValue}/10` : ""}
            </span>
        </div>
    );
};

export default MovieRating;