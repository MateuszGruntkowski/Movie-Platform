// const TMDB_API_KEY = process.env.REACT_APP_TMDB_API_KEY;
// const TMDB_BASE_URL = "https://api.themoviedb.org/3";
// const TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w92";

// export const movieSearchService = {
//   async searchMovies(query, limit = 8) {
//     try {
//       const options = {
//         method: "GET",
//         headers: {
//           accept: "application/json",
//           Authorization: `Bearer ${TMDB_API_KEY}`,
//         },
//       };

//       const response = await fetch(
//         `${TMDB_BASE_URL}/search/movie?query=${encodeURIComponent(
//           query
//         )}&language=en-US&page=1`,
//         options
//       );

//       if (!response.ok) {
//         throw new Error("Failed to fetch movies");
//       }

//       const data = await response.json();
//       return data.results.slice(0, limit);
//     } catch (error) {
//       console.error("Error searching movies:", error);
//       throw error;
//     }
//   },

//   getImageUrl(posterPath) {
//     return posterPath ? `${TMDB_IMAGE_BASE_URL}${posterPath}` : null;
//   },
// };

import api from "../api/axiosConfig";

// const API_BASE_URL = "http://localhost:8080/api/v1/movies";

export const movieSearchService = {
  async searchMovies(query, limit = 8) {
    try {
      // Wywołaj swoje API zamiast TMDB bezpośrednio
      const response = await api.get(`/v1/movies/search`, {
        params: { query, limit },
      });
      return response.data;
    } catch (error) {
      console.error("Error searching movies:", error);
      throw error;
    }
  },

  // Ta metoda może zostać jako utility function
  getImageUrl(posterPath, size = "w500") {
    if (!posterPath) return null;
    return `https://image.tmdb.org/t/p/${size}${posterPath}`;
  },

  async searchMoviesDetailed(query, page = 1) {
    try {
      // Wywołaj swoje API zamiast TMDB bezpośrednio
      const response = await api.get(`/v1/movies/search-results`, {
        params: { query, page },
      });
      return response.data;
    } catch (error) {
      console.error("Error searching movies:", error);
      throw error;
    }
  },

  // async searchMoviesDetailed(query, page = 1) {
  //   try {
  //     const options = {
  //       method: "GET",
  //       headers: {
  //         accept: "application/json",
  //         Authorization: `Bearer ${process.env.REACT_APP_TMDB_API_KEY}`,
  //       },
  //     };

  //     const response = await fetch(
  //       `https://api.themoviedb.org/3/search/movie?query=${encodeURIComponent(
  //         query
  //       )}&language=en-US&page=${page}`,
  //       options
  //     );

  //     if (!response.ok) {
  //       throw new Error("Failed to fetch movies");
  //     }

  //     const data = await response.json();
  //     return {
  //       results: data.results,
  //       totalPages: data.total_pages,
  //       totalResults: data.total_results,
  //       page: data.page,
  //     };
  //   } catch (error) {
  //     console.error("Error searching movies:", error);
  //     throw error;
  //   }
  // },
};
