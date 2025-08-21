const TMDB_API_KEY = process.env.REACT_APP_TMDB_API_KEY;
const TMDB_BASE_URL = "https://api.themoviedb.org/3";
const TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w92";

export const movieSearchService = {
  async searchMovies(query, limit = 8) {
    try {
      const options = {
        method: "GET",
        headers: {
          accept: "application/json",
          Authorization: `Bearer ${TMDB_API_KEY}`,
        },
      };

      const response = await fetch(
        `${TMDB_BASE_URL}/search/movie?query=${encodeURIComponent(
          query
        )}&language=en-US&page=1`,
        options
      );

      if (!response.ok) {
        throw new Error("Failed to fetch movies");
      }

      const data = await response.json();
      return data.results.slice(0, limit);
    } catch (error) {
      console.error("Error searching movies:", error);
      throw error;
    }
  },

  getImageUrl(posterPath) {
    return posterPath ? `${TMDB_IMAGE_BASE_URL}${posterPath}` : null;
  },
};
