import api from "../api/axiosConfig";

export const userService = {
    async getCurrentUser() {
        try {
            const response = await api.get("/v1/users/me");
            return response.data;
        } catch (error) {
            console.error("Error fetching current user:", error);
            throw error;
        }
    },

    async toggleWatchlistStatus(movieId, listType) {
        try {
            await api.put(`/v1/users/watchlist/toggle/${movieId}`, null, {
                params: { listType },
            });
        } catch (error) {
            console.error("Error toggling movie status:", error);
            throw error;
        }
    },
};