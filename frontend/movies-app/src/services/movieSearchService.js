import api from "../api/axiosConfig";

export const movieSearchService = {
  async searchMovies(query, page = 1) {
    try {
      const response = await api.get(`/v1/movies/search`, {
        params: { query, page },
      });
      return response.data;
    } catch (error) {
      console.error("Error searching movies:", error);
      throw error;
    }
  },
};