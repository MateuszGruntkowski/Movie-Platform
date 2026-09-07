import { useUserProfile } from "./hooks/useUserProfile";
import ProfileHeader from "./ProfileHeader";
import ProfileStats from "./ProfileStats";
import ProfileReviewsSection from "./ProfileReviewsSection";
import ProfileRatingsSection from "./ProfileRatingsSection";

const ProfileContent = ({ username, isOwnProfile }) => {
    const { profile, setProfile, isLoading, error, isUnauthorized } = useUserProfile(username);

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

    const decrementReviewsCount = () => {
        setProfile((prev) =>
            prev ? { ...prev, reviewsCount: Math.max(prev.reviewsCount - 1, 0) } : prev
        );
    };

    return (
        <div className="profile-container">
            <ProfileHeader profile={profile} isOwnProfile={isOwnProfile} onProfileUpdate={setProfile} />
            <ProfileStats profile={profile} />
            <ProfileReviewsSection
                username={username}
                isOwnProfile={isOwnProfile}
                onReviewDeleted={decrementReviewsCount}
            />
            <ProfileRatingsSection username={username} isOwnProfile={isOwnProfile} />
        </div>
    );
};

export default ProfileContent;