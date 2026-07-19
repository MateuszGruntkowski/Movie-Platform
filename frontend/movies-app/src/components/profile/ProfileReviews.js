import { Link } from "react-router-dom";

const ProfileReviews = ({ reviews }) => {
    if (!reviews || reviews.length === 0) {
        return (
            <div className="profile-empty">
                <p>You haven't written any reviews yet.</p>
            </div>
        );
    }

    return (
        <div className="profile-list">
            {reviews.map((review) => (
                <Link
                    to={`/Details/${review.movie.tmdbId}`}
                    className="profile-list-item"
                    key={review.id}
                >
                    <img
                        src={
                            review.movie.posterPath ||
                            "https://via.placeholder.com/80x120/2A2D3A/FFD700?text=No+Poster"
                        }
                        alt={review.movie.title}
                        className="profile-list-poster"
                    />
                    <div className="profile-list-content">
                        <h4 className="profile-list-title">{review.movie.title}</h4>
                        <p className="profile-list-body">{review.body}</p>
                        <span className="profile-list-date">
              {new Date(review.createdAt).toLocaleDateString()}
            </span>
                    </div>
                </Link>
            ))}
        </div>
    );
};

export default ProfileReviews;