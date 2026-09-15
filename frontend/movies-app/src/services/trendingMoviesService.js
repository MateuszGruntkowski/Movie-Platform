import api from "../api/axiosConfig";

export const trendingMoviesService = {
    getTrending: async () => {
        const response = await api.get("v1/movies/trending");
        return response.data;
    },
};