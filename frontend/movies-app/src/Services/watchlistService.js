import api from "../api/axiosConfig";

export const watchlistService = {
    async getMoviesToWatch({ page = 0, size = 10 } = {}) {
        try {
            const response = await api.get("/v1/users/watchlist/to-watch", {
                params: { page, size },
            });
            return response.data;
        } catch (error) {
            console.error("Error fetching movies to watch:", error);
            throw error;
        }
    },

    async getMoviesWatched({ page = 0, size = 10 } = {}) {
        try {
            const response = await api.get("/v1/users/watchlist/watched", {
                params: { page, size },
            });
            return response.data;
        } catch (error) {
            console.error("Error fetching watched movies:", error);
            throw error;
        }
    },
};