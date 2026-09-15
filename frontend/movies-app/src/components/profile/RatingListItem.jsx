import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faStar } from "@fortawesome/free-solid-svg-icons";
import ProfileListItem from "./ProfileListItem";
import { formatDate } from "./utils/dateUtils.js";

const RatingListItem = ({ rating }) => (
    <ProfileListItem
        tmdbId={rating.movie.tmdbId}
        posterPath={rating.movie.posterPath}
        title={rating.movie.title}
    >
        <h4 className="profile-list-title">{rating.movie.title}</h4>
        <span className="profile-list-rating">
            <FontAwesomeIcon icon={faStar} className="profile-rating-star" /> {rating.rating}/10
        </span>
        <span className="profile-list-date">{formatDate(rating.createdAt)}</span>
    </ProfileListItem>
);

export default RatingListItem;