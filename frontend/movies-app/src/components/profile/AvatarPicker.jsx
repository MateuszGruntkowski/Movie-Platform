import { getAvatarUrl } from "../../utils/avatarUtils";

const AVAILABLE_AVATARS = [
    "/avatars/avatar1.png",
    "/avatars/avatar2.png",
    "/avatars/avatar3.png",
    "/avatars/avatar4.png",
    "/avatars/avatar5.png",
    "/avatars/avatar6.png",
    "/avatars/avatar7.png",
    "/avatars/avatar8.png",
    "/avatars/avatar9.png",
    "/avatars/avatar10.png",
    "/avatars/avatar11.png",
    "/avatars/avatar12.png",
    "/avatars/avatar13.png",
    "/avatars/avatar14.png",
    "/avatars/avatar15.png",
    "/avatars/avatar16.png"
];

const AvatarPicker = ({ currentAvatarPath, onSelect, onClose }) => {
    return (
        <div className="avatar-picker-overlay" onClick={onClose}>
            <div className="avatar-picker-modal" onClick={(e) => e.stopPropagation()}>
                <h2 className="avatar-picker-title">Choose avatar</h2>
                <div className="avatar-picker-grid">
                    {AVAILABLE_AVATARS.map((path) => (
                        <button
                            key={path}
                            className={`avatar-picker-option ${
                                path === currentAvatarPath ? "avatar-picker-option-active" : ""
                            }`}
                            onClick={() => onSelect(path)}
                        >
                            <img src={getAvatarUrl(path)} alt="avatar option" />
                        </button>
                    ))}
                </div>
                <button className="avatar-picker-close" onClick={onClose}>
                    Cancel
                </button>
            </div>
        </div>
    );
};

export default AvatarPicker;