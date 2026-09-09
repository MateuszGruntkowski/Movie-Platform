import { userProfileService } from "../../services/userProfileService";
import { usePaginatedProfileData } from "./hooks/usePaginatedProfileData.js";
import ProfileSectionHeader from "./ProfileSectionHeader";
import RatingListItem from "./RatingListItem";
import LoadMoreButton from "./LoadMoreButton";
import "./ProfileRatingsSection.css"

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

            {isLoading && items.length === 0 ? (
                <p className="profile-loading">Loading ratings...</p>
            ) : items.length === 0 ? (
                <div className="profile-empty">
                    <p>You haven't rated any movies yet.</p>
                </div>
            ) : (
                <div className="profile-list">
                    {items.map((rating) => (
                        <RatingListItem key={rating.id} rating={rating} />
                    ))}
                    <LoadMoreButton hasMore={hasMore} isLoadingMore={isLoadingMore} onLoadMore={loadMore} />
                </div>
            )}
        </div>
    );
};

export default ProfileRatingsSection;