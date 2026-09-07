import { useState } from "react";
import AvatarPicker from "./AvatarPicker";
import { getAvatarUrl } from "../../utils/avatarUtils";
import { userProfileService } from "../../services/userProfileService";
import "./ProfileHeader.css"

const ProfileHeader = ({ profile, isOwnProfile, onProfileUpdate }) => {
    const [isPickerOpen, setIsPickerOpen] = useState(false);
    const avatarUrl = getAvatarUrl(profile.avatarPath);

    const handleAvatarSelect = async (avatarPath) => {
        try {
            const updated = await userProfileService.updateAvatar(avatarPath);
            onProfileUpdate(updated);
            setIsPickerOpen(false);
        } catch (err) {
            console.error("Error updating avatar:", err);
        }
    };

    return (
        <>
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
        </>
    );
};

export default ProfileHeader;