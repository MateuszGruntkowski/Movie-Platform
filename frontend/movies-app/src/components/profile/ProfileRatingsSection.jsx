import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faStar } from "@fortawesome/free-solid-svg-icons";
import { userProfileService } from "../../services/userProfileService";
import { usePaginatedProfileData } from "./hooks/usePaginatedProfileData.js";
import ProfileSectionHeader from "./ProfileSectionHeader";
import ProfileEntryList from "./ProfileEntryList";
import ProfileListItem from "./ProfileListItem";
import { formatDate } from "./utils/dateUtils.js";
import "./ProfileRatingSection.css"

const RATINGS_PAGE_SIZE = 5;
const SORT_OPTIONS = [
    { value: "newest", label: "Newest" },
    { value: "oldest", label: "Oldest" },
    { value: "highest", label: "Highest" },
    { value: "lowest", label: "Lowest" },
];

const ProfileRatingsSection = ({ username, isOwnProfile }) => {
    const { items, sort, changeSort, hasMore, isLoading, isLoadingMore, loadMore } =
        usePaginatedProfileData(
            (page, size, sortValue) =>
                userProfileService.getUserRatings(username, { page, size, sort: sortValue }),
            RATINGS_PAGE_SIZE
        );

    return (
        <div className="profile-section">
            <ProfileSectionHeader
                title={isOwnProfile ? "Your Ratings" : "Ratings"}
                sortValue={sort}
                onSortChange={changeSort}
                sortOptions={SORT_OPTIONS}
            />
            <ProfileEntryList
                items={items}
                isLoading={isLoading}
                loadingText="Loading ratings..."
                emptyText="You haven't rated any movies yet."
                hasMore={hasMore}
                isLoadingMore={isLoadingMore}
                onLoadMore={loadMore}
                renderItem={(rating) => (
                    <ProfileListItem
                        key={rating.id}
                        tmdbId={rating.movie.tmdbId}
                        posterPath={rating.movie.posterPath}
                        title={rating.movie.title}
                    >
                        <h4 className="profile-list-title">{rating.movie.title}</h4>
                        <span className="profile-list-rating">
                            <FontAwesomeIcon icon={faStar} className="profile-rating-star" />{" "}
                            {rating.rating}/10
                        </span>
                        <span className="profile-list-date">{formatDate(rating.createdAt)}</span>
                    </ProfileListItem>
                )}
            />
        </div>
    );
};

export default ProfileRatingsSection;