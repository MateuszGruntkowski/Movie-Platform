import api from "../api/axiosConfig";

export const userProfileService = {
    async getUserProfile(username) {
        try {
            const response = await api.get(`/v1/users/${username}/profile`);
            return response.data;
        } catch (error) {
            console.error("Error fetching user profile:", error);
            throw error;
        }
    },

    async getUserReviews(username, { page = 0, size = 10, sort = "newest" } = {}) {
        try {
            const response = await api.get(`/v1/users/${username}/reviews`, {
                params: { page, size, sort },
            });
            return response.data;
        } catch (error) {
            console.error("Error fetching user reviews:", error);
            throw error;
        }
    },

    async getUserRatings(username, { page = 0, size = 10, sort = "newest" } = {}) {
        try {
            const response = await api.get(`/v1/users/${username}/ratings`, {
                params: { page, size, sort },
            });
            return response.data;
        } catch (error) {
            console.error("Error fetching user ratings:", error);
            throw error;
        }
    },

    async updateAvatar(avatarPath) {
        try {
            const response = await api.put("/v1/users/me/avatar", { avatarPath });
            return response.data;
        } catch (error) {
            console.error("Error updating avatar:", error);
            throw error;
        }
    },
};