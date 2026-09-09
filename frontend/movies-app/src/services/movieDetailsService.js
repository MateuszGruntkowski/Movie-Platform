import api from "../api/axiosConfig";

export const movieDetailsService = {
  async getMovieDetails(movieId) {
    try {
      const response = await api.get(`/v1/movies/${movieId}/details`);
      return response.data;
    } catch (error) {
      console.error("Error fetching movie details:", error);
      throw error;
    }
  },
};