import { useEffect, useState } from "react";
import { userProfileService } from "../../Services/userProfileService";
import ProfileStats from "./ProfileStats";
import ProfileReviews from "./ProfileReviews";
import ProfileRatings from "./ProfileRatings";
import "./Profile.css";

const Profile = () => {
    const [profile, setProfile] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        setIsLoading(true);
        setError(null);

        userProfileService
            .getMyProfile()
            .then((data) => setProfile(data))
            .catch((err) => {
                console.error("Error fetching profile:", err);
                setError("Nie udało się załadować profilu.");
            })
            .finally(() => setIsLoading(false));
    }, []);

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

    return (
        <div className="profile-container">
            <div className="profile-header">
                <div className="profile-avatar">
                    {profile.username?.charAt(0).toUpperCase()}
                </div>
                <h1 className="profile-username">{profile.username}</h1>
            </div>

            <ProfileStats profile={profile} />

            <div className="profile-section">
                <h2 className="profile-section-title">Your Reviews</h2>
                <ProfileReviews reviews={profile.reviews} />
            </div>

            <div className="profile-section">
                <h2 className="profile-section-title">Your Ratings</h2>
                <ProfileRatings ratings={profile.ratings} />
            </div>
        </div>
    );
};

export default Profile;