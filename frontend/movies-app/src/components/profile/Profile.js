import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { userProfileService } from "../../Services/userProfileService";
import { useUser } from "../context/UserContext";
import ProfileStats from "./ProfileStats";
import ProfileReviews from "./ProfileReviews";
import ProfileRatings from "./ProfileRatings";
import AvatarPicker from "./AvatarPicker";
import { getAvatarUrl } from "../../utils/avatarUtils";
import "./Profile.css";

const REVIEWS_PAGE_SIZE = 5;
const RATINGS_PAGE_SIZE = 5;

const Profile = () => {
    const { username } = useParams();
    const { user } = useUser();
    const isOwnProfile = user?.username === username;

    const [profile, setProfile] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isUnauthorized, setIsUnauthorized] = useState(false);
    const [isPickerOpen, setIsPickerOpen] = useState(false);

    // Recenzje
    const [reviews, setReviews] = useState([]);
    const [reviewsPage, setReviewsPage] = useState(0);
    const [reviewsSort, setReviewsSort] = useState("newest");
    const [hasMoreReviews, setHasMoreReviews] = useState(false);
    const [isLoadingReviews, setIsLoadingReviews] = useState(true);
    const [isLoadingMoreReviews, setIsLoadingMoreReviews] = useState(false);

    // Oceny
    const [ratings, setRatings] = useState([]);
    const [ratingsPage, setRatingsPage] = useState(0);
    const [ratingsSort, setRatingsSort] = useState("newest");
    const [hasMoreRatings, setHasMoreRatings] = useState(false);
    const [isLoadingRatings, setIsLoadingRatings] = useState(true);
    const [isLoadingMoreRatings, setIsLoadingMoreRatings] = useState(false);

    // Statystyki profilu
    useEffect(() => {
        if (!username) return;

        setIsLoading(true);
        setError(null);
        setIsUnauthorized(false);

        userProfileService
            .getUserProfile(username)
            .then((data) => setProfile(data))
            .catch((err) => {
                console.error("Error fetching profile:", err);
                if (err.response?.status === 401) {
                    setIsUnauthorized(true);
                } else {
                    setError("Failed to load profile. Please try again later.");
                }
            })
            .finally(() => setIsLoading(false));
    }, [username]);

    // Pierwsza strona recenzji (resetuje się przy zmianie sortowania)
    useEffect(() => {
        if (!username) return;

        setIsLoadingReviews(true);
        setReviewsPage(0);

        userProfileService
            .getUserReviews(username, { page: 0, size: REVIEWS_PAGE_SIZE, sort: reviewsSort })
            .then((data) => {
                setReviews(data.content);
                setHasMoreReviews(!data.last);
            })
            .catch((err) => console.error("Error fetching reviews:", err))
            .finally(() => setIsLoadingReviews(false));
    }, [username, reviewsSort]);

    // Pierwsza strona ocen (resetuje się przy zmianie sortowania)
    useEffect(() => {
        if (!username) return;

        setIsLoadingRatings(true);
        setRatingsPage(0);

        userProfileService
            .getUserRatings(username, { page: 0, size: RATINGS_PAGE_SIZE, sort: ratingsSort })
            .then((data) => {
                setRatings(data.content);
                setHasMoreRatings(!data.last);
            })
            .catch((err) => console.error("Error fetching ratings:", err))
            .finally(() => setIsLoadingRatings(false));
    }, [username, ratingsSort]);

    const loadMoreReviews = async () => {
        const nextPage = reviewsPage + 1;
        setIsLoadingMoreReviews(true);
        try {
            const data = await userProfileService.getUserReviews(username, {
                page: nextPage,
                size: REVIEWS_PAGE_SIZE,
                sort: reviewsSort,
            });
            setReviews((prev) => [...prev, ...data.content]);
            setReviewsPage(nextPage);
            setHasMoreReviews(!data.last);
        } catch (err) {
            console.error("Error loading more reviews:", err);
        } finally {
            setIsLoadingMoreReviews(false);
        }
    };

    const loadMoreRatings = async () => {
        const nextPage = ratingsPage + 1;
        setIsLoadingMoreRatings(true);
        try {
            const data = await userProfileService.getUserRatings(username, {
                page: nextPage,
                size: RATINGS_PAGE_SIZE,
                sort: ratingsSort,
            });
            setRatings((prev) => [...prev, ...data.content]);
            setRatingsPage(nextPage);
            setHasMoreRatings(!data.last);
        } catch (err) {
            console.error("Error loading more ratings:", err);
        } finally {
            setIsLoadingMoreRatings(false);
        }
    };

    const handleAvatarSelect = async (avatarPath) => {
        try {
            const updated = await userProfileService.updateAvatar(avatarPath);
            setProfile(updated);
            setIsPickerOpen(false);
        } catch (err) {
            console.error("Error updating avatar:", err);
        }
    };

    if (isUnauthorized) {
        return (
            <div className="profile-container">
                <p className="profile-error">You must log in to view this profile.</p>
            </div>
        );
    }

    if (isLoading) {
        return (
            <div className="profile-container">
                <p className="profile-loading">Loading profile...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="profile-container">
                <p className="profile-error">{error}</p>
            </div>
        );
    }

    if (!profile) return null;

    const avatarUrl = getAvatarUrl(profile.avatarPath);

    return (
        <div className="profile-container">
            <div className="profile-header">
                {isOwnProfile ? (
                    <button
                        className="profile-avatar-button"
                        onClick={() => setIsPickerOpen(true)}
                        aria-label="Change Avatar"
                    >
                        {avatarUrl ? (
                            <img src={avatarUrl} alt="avatar" className="profile-avatar-img" />
                        ) : (
                            <div className="profile-avatar">
                                {profile.username?.charAt(0).toUpperCase()}
                            </div>
                        )}
                    </button>
                ) : avatarUrl ? (
                    <img src={avatarUrl} alt="avatar" className="profile-avatar-img" />
                ) : (
                    <div className="profile-avatar">
                        {profile.username?.charAt(0).toUpperCase()}
                    </div>
                )}
                <h1 className="profile-username">{profile.username}</h1>
            </div>

            {isOwnProfile && isPickerOpen && (
                <AvatarPicker
                    currentAvatarPath={profile.avatarPath}
                    onSelect={handleAvatarSelect}
                    onClose={() => setIsPickerOpen(false)}
                />
            )}

            <ProfileStats profile={profile} />

            <div className="profile-section">
                <div className="profile-section-header">
                    <h2 className="profile-section-title">
                        {isOwnProfile ? "Your Reviews" : "Reviews"}
                    </h2>
                    <select
                        className="profile-sort-select"
                        value={reviewsSort}
                        onChange={(e) => setReviewsSort(e.target.value)}
                    >
                        <option value="newest">Newest</option>
                        <option value="oldest">Oldest</option>
                    </select>
                </div>
                <ProfileReviews
                    reviews={reviews}
                    isLoading={isLoadingReviews}
                    hasMore={hasMoreReviews}
                    isLoadingMore={isLoadingMoreReviews}
                    onLoadMore={loadMoreReviews}
                />
            </div>

            <div className="profile-section">
                <div className="profile-section-header">
                    <h2 className="profile-section-title">
                        {isOwnProfile ? "Your Ratings" : "Ratings"}
                    </h2>
                    <select
                        className="profile-sort-select"
                        value={ratingsSort}
                        onChange={(e) => setRatingsSort(e.target.value)}
                    >
                        <option value="newest">Newest</option>
                        <option value="oldest">Oldest</option>
                        <option value="highest">Highest</option>
                        <option value="lowest">Lowest</option>
                    </select>
                </div>
                <ProfileRatings
                    ratings={ratings}
                    isLoading={isLoadingRatings}
                    hasMore={hasMoreRatings}
                    isLoadingMore={isLoadingMoreRatings}
                    onLoadMore={loadMoreRatings}
                />
            </div>
        </div>
    );
};

export default Profile;