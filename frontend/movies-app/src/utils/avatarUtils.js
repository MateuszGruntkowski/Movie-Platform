// src/utils/avatarUtils.js
const API_ORIGIN = "http://localhost:8080"; // bez /api

export const getAvatarUrl = (avatarPath) => {
    if (!avatarPath) return null;
    return `${API_ORIGIN}${avatarPath}`;
};