import { userProfileService } from "../../services/userProfileService";
import { reviewsService } from "../../services/reviewsService";
import { usePaginatedProfileData } from "./hooks/usePaginatedProfileData.js";
import ProfileSectionHeader from "./ProfileSectionHeader";
import ReviewListItem from "./ReviewListItem";
import LoadMoreButton from "./LoadMoreButton";
import "./ProfileReviewsSection.css"

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

            {isLoading && items.length === 0 ? (
                <p className="profile-loading">Loading reviews...</p>
            ) : items.length === 0 ? (
                <div className="profile-empty">
                    <p>You haven't written any reviews yet.</p>
                </div>
            ) : (
                <div className="profile-list">
                    {items.map((review) => (
                        <ReviewListItem
                            key={review.id}
                            review={review}
                            isOwnProfile={isOwnProfile}
                            onDelete={handleDelete}
                        />
                    ))}
                    <LoadMoreButton hasMore={hasMore} isLoadingMore={isLoadingMore} onLoadMore={loadMore} />
                </div>
            )}
        </div>
    );
};

export default ProfileReviewsSection;