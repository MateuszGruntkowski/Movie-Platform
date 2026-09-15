import api from "../api/axiosConfig";

export const movieRatingService = {
    async getMyRating(movieId) {
        try {
            const response = await api.get(`/v1/movies/${movieId}/ratings/me`);
            // Backend zwraca 204 No Content, gdy użytkownik jeszcze nie ocenił filmu
            if (response.status === 204 || !response.data) return null;
            return response.data;
        } catch (error) {
            console.error("Error fetching user rating:", error);
            throw error;
        }
    },

    async updateRating(movieId, rating) {
        try {
            const response = await api.put(`/v1/movies/${movieId}/ratings/me`, {
                rating,
            });
            return response.data;
        } catch (error) {
            console.error("Error updating rating:", error);
            throw error;
        }
    },
};