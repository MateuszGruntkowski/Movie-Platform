import api from "../api/axiosConfig";

export const reviewsService = {
    async getReviewsForMovie(movieId, { page = 0, size = 10, sort = "createdAt,desc" } = {}) {
        try {
            const response = await api.get(`/v1/reviews/${movieId}`, {
                params: { page, size, sort },
            });
            return response.data;
        } catch (error) {
            console.error("Error fetching reviews:", error);
            throw error;
        }
    },

    async createReview(movieId, reviewBody) {
        try {
            const response = await api.post(`/v1/reviews/create/${movieId}`, {
                reviewBody,
            });
            return response.data;
        } catch (error) {
            console.error("Error adding review:", error);
            throw error;
        }
    },


    async deleteReview(reviewId) {
        try {
            await api.delete(`/v1/reviews/${reviewId}`);
        } catch (error) {
            console.error("Error deleting review:", error);
            throw error;
        }
    },
};