import { Form, Button } from "react-bootstrap";
import { Link, useParams } from "react-router-dom";
import React, { use } from "react";
import { useUser } from "../context/UserContext";

const ReviewForm = ({ handleSubmit, labelText, revText }) => {
  const params = useParams();
  let movieId = params.movieId;

  const { user } = useUser();

  return (
    <div className="review-form-container">
      {user ? (
        <>
          <div className="form-group">
            <label className="form-label">{labelText}</label>
            <textarea
              ref={revText}
              rows={4}
              placeholder="Share your opinion about this movie..."
              className="review-textarea"
            />
          </div>
          <button onClick={handleSubmit} className="submit-btn">
            Add review
          </button>
        </>
      ) : (
        <div className="login-prompt">
          <p>Please log in to write a review.</p>
          <Button
            variant="outline-info"
            as={Link}
            to="/login"
            state={{ from: `/Reviews/${movieId}` }}
          >
            Login
          </Button>
        </div>
      )}
    </div>
  );
};

export default ReviewForm;
