const TMDB_API_KEY = process.env.REACT_APP_TMDB_API_KEY;
const TMDB_BASE_URL = "https://api.themoviedb.org/3";
const TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"; // Większy rozmiar dla szczegółów
const TMDB_BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/original"; // Większy rozmiar dla backdrops

export const movieDetailsService = {
  async getMovieDetails(movieId) {
    try {
      const options = {
        method: "GET",
        headers: {
          accept: "application/json",
          Authorization: `Bearer ${TMDB_API_KEY}`,
        },
      };

      const response = await fetch(
        `${TMDB_BASE_URL}/movie/${movieId}?language=en-US`,
        options
      );

      if (!response.ok) {
        throw new Error(`Failed to fetch movie details for ID: ${movieId}`);
      }

      const data = await response.json();
      return data;
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
      const options = {
        method: "GET",
        headers: {
          accept: "application/json",
          Authorization: `Bearer ${TMDB_API_KEY}`,
        },
      };

      const response = await fetch(
        `${TMDB_BASE_URL}/movie/${movieId}/videos?language=en-US`,
        options
      );

      if (!response.ok) {
        throw new Error(`Failed to fetch movie videos for ID: ${movieId}`);
      }

      const data = await response.json();
      return data.results;
    } catch (error) {
      console.error("Error fetching movie videos:", error);
      throw error;
    }
  },

  async getTrailerUrl(movieId) {
    try {
      const videos = await this.getMovieVideos(movieId);

      const trailer =
        videos.find(
          (video) =>
            video.type === "Trailer" &&
            video.site === "YouTube" &&
            video.official === true
        ) ||
        videos.find(
          (video) => video.type === "Trailer" && video.site === "YouTube"
        );

      if (trailer) {
        return `https://www.youtube.com/watch?v=${trailer.key}`;
      }

      return null;
    } catch (error) {
      console.error("Error getting trailer URL:", error);
      return null;
    }
  },

  async getMovieBackdrops(movieId, limit = 10) {
    try {
      const movieDetails = await this.getMovieDetails(movieId);
      const backdrops = [];

      // Dodaj główny backdrop filmu
      if (movieDetails.backdrop_path) {
        backdrops.push(TMDB_BACKDROP_BASE_URL + movieDetails.backdrop_path);
      }

      // Sprawdź czy film należy do kolekcji i pobierz dodatkowe backdrops
      if (
        movieDetails.belongs_to_collection &&
        movieDetails.belongs_to_collection.id
      ) {
        const collectionBackdrops = await this.getCollectionBackdrops(
          movieDetails.belongs_to_collection.id
        );

        // Dodaj backdrops z kolekcji (filtruj duplikaty)
        collectionBackdrops.forEach((backdrop) => {
          if (!backdrops.includes(backdrop)) {
            backdrops.push(TMDB_BACKDROP_BASE_URL + backdrop);
          }
        });
      }

      return backdrops.slice(0, limit);
    } catch (error) {
      console.error("Error fetching movie backdrops:", error);
      return [];
    }
  },

  async getCollectionBackdrops(collectionId) {
    try {
      const options = {
        method: "GET",
        headers: {
          accept: "application/json",
          Authorization: `Bearer ${TMDB_API_KEY}`,
        },
      };

      const response = await fetch(
        `${TMDB_BACKDROP_BASE_URL}/collection/${collectionId}/images`,
        options
      );

      if (!response.ok) {
        throw new Error(
          `Failed to fetch collection images for ID: ${collectionId}`
        );
      }

      const data = await response.json();
      return data.backdrops.map((backdrop) => backdrop.file_path);
    } catch (error) {
      console.error("Error fetching collection backdrops:", error);
      return [];
    }
  },
};
