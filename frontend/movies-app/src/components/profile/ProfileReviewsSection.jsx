import { userProfileService } from "../../services/userProfileService";
import { reviewsService } from "../../services/reviewsService";
import { usePaginatedProfileData } from "./hooks/usePaginatedProfileData.js";
import ProfileSectionHeader from "./ProfileSectionHeader";
import ProfileEntryList from "./ProfileEntryList";
import ProfileListItem from "./ProfileListItem";
import { formatDate } from "./utils/dateUtils.js";

const REVIEWS_PAGE_SIZE = 5;
const SORT_OPTIONS = [
    { value: "newest", label: "Newest" },
    { value: "oldest", label: "Oldest" },
];

const ProfileReviewsSection = ({ username, isOwnProfile, onReviewDeleted }) => {
    const { items, setItems, sort, changeSort, hasMore, isLoading, isLoadingMore, loadMore } =
        usePaginatedProfileData(
            (page, size, sortValue) =>
                userProfileService.getUserReviews(username, { page, size, sort: sortValue }),
            REVIEWS_PAGE_SIZE
        );

    const handleDelete = async (reviewId) => {
        try {
            await reviewsService.deleteReview(reviewId);
            setItems((prev) => prev.filter((r) => r.id !== reviewId));
            onReviewDeleted?.();
        } catch (err) {
            console.error("Error deleting review:", err);
        }
    };

    return (
        <div className="profile-section">
            <ProfileSectionHeader
                title={isOwnProfile ? "Your Reviews" : "Reviews"}
                sortValue={sort}
                onSortChange={changeSort}
                sortOptions={SORT_OPTIONS}
            />
            <ProfileEntryList
                items={items}
                isLoading={isLoading}
                loadingText="Loading reviews..."
                emptyText="You haven't written any reviews yet."
                hasMore={hasMore}
                isLoadingMore={isLoadingMore}
                onLoadMore={loadMore}
                renderItem={(review) => (
                    <ProfileListItem
                        key={review.id}
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
                                        handleDelete(review.id);
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
                )}
            />
        </div>
    );
};

export default ProfileReviewsSection;