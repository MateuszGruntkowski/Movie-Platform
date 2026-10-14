import { Link, useParams } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import "./ReviewForm.css";

const ReviewForm = ({ handleSubmit, labelText, revText, isSubmitting = false }) => {
  const { movieId } = useParams();
  const { user } = useAuth();

  if (!user) {
    return (
        <div className="review-form review-form--guest">
          <p>
            <Link
                to="/login"
                state={{ from: `/Details/${movieId}` }}
                className="review-login-link"
            >
              Log in
            </Link>{" "}
            to write a review.
          </p>
        </div>
    );
  }

  return (
      <form className="review-form" onSubmit={handleSubmit}>
        <label className="review-form-label" htmlFor="review-text">
          {labelText}
        </label>
        <textarea
            id="review-text"
            ref={revText}
            rows={4}
            placeholder="Share your opinion about this movie..."
            className="review-textarea"
        />
        <button type="submit" className="review-submit-btn" disabled={isSubmitting}>
          {isSubmitting ? "Posting…" : "Post review"}
        </button>
      </form>
  );
};

export default ReviewForm;