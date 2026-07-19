import { getAvatarUrl } from "../../utils/avatarUtils";

const AVAILABLE_AVATARS = [
    "/avatars/avatar1.png",
    // kolejne dodasz tutaj
];

const AvatarPicker = ({ currentAvatarPath, onSelect, onClose }) => {
    return (
        <div className="avatar-picker-overlay" onClick={onClose}>
            <div className="avatar-picker-modal" onClick={(e) => e.stopPropagation()}>
                <h2 className="avatar-picker-title">Wybierz avatar</h2>
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
                    Anuluj
                </button>
            </div>
        </div>
    );
};

export default AvatarPicker;