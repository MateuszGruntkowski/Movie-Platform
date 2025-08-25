import api from "../api/axiosConfig";

export const movieSearchService = {
  async searchMovies(query, limit = 8) {
    try {
      const response = await api.get(`/v1/movies/search`, {
        params: { query, limit },
      });
      return response.data;
    } catch (error) {
      console.error("Error searching movies:", error);
      throw error;
    }
  },

  getImageUrl(posterPath, size = "w500") {
    if (!posterPath) return null;
    return `https://image.tmdb.org/t/p/${size}${posterPath}`;
  },

  async searchMoviesDetailed(query, page = 1) {
    try {
      const response = await api.get(`/v1/movies/search-results`, {
        params: { query, page },
      });
      return response.data;
    } catch (error) {
      console.error("Error searching movies:", error);
      throw error;
    }
  },
};
