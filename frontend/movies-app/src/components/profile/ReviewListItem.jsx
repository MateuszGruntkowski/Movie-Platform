import ProfileListItem from "./ProfileListItem";
import { formatDate } from "./utils/dateUtils.js";

const ReviewListItem = ({ review, isOwnProfile, onDelete }) => (
    <ProfileListItem
        tmdbId={review.movie.tmdbId}
        posterPath={review.movie.posterPath}
        title={review.movie.title}
    >
        <div className="profile-list-header">
            <h4 className="profile-list-title">{review.movie.title}</h4>
            {isOwnProfile && (
                <button
                    type="button"
                    className="review-delete-btn"
                    onClick={(e) => {
                        e.stopPropagation();
                        onDelete(review.id);
                    }}
                    aria-label="Delete review"
                >
                    Delete
                </button>
            )}
        </div>
        <p className="profile-list-body">{review.body}</p>
        <span className="profile-list-date">{formatDate(review.createdAt)}</span>
    </ProfileListItem>
);

export default ReviewListItem;