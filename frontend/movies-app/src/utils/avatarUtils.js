const API_ORIGIN = "http://localhost:8080";

export const getAvatarUrl = (avatarPath) => {
    if (!avatarPath) return null;
    return `${API_ORIGIN}${avatarPath}`;
};