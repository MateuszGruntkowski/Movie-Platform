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

  getImageUrl(imagePath, size = "w500") {
    if (!imagePath) return null;
    return `https://image.tmdb.org/t/p/${size}${imagePath}`;
  },

  async getMovieVideos(movieId) {
    try {
      const response = await api.get(`/v1/movies/${movieId}/videos`);
      return response.data;
    } catch (error) {
      console.error("Error fetching movie videos:", error);
      throw error;
    }
  },

  async getTrailerUrl(movieId) {
    try {
      const response = await api.get(`/v1/movies/${movieId}/trailer`);
      return response.data.trailerUrl;
    } catch (error) {
      console.error("Error getting trailer URL:", error);
      return null;
    }
  },

  async getMovieBackdrops(movieId, limit = 10) {
    try {
      const response = await api.get(
        `/v1/movies/${movieId}/backdrops?limit=${limit}`
      );
      return response.data.backdrops;
    } catch (error) {
      console.error("Error fetching movie backdrops:", error);
      return [];
    }
  },
};
