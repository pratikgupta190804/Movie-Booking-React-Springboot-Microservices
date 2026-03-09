import axios from "axios";
import { API_CONFIG } from "../config/constants";
import { setupInterceptors } from "./axiosInterceptors";

const movieClient = axios.create({
  baseURL: API_CONFIG.MOVIES_SERVICE,
  headers: {
    "Content-Type": "application/json",
  },
});

// Setup auth token and refresh interceptors
setupInterceptors(movieClient);

export const movieService = {
  async getAllMovies() {
    const response = await movieClient.get("/movies");
    return response.data;
  },

  async getMovieById(movieId) {
    const response = await movieClient.get(`/movies/${movieId}`);
    return response.data;
  },

  async getMovieBySlug(slug) {
    const response = await movieClient.get(`/movies/slug/${slug}`);
    return response.data;
  },

  async getMoviesByRating(rating) {
    const response = await movieClient.get(`/movies/rating/${rating}`);
    return response.data;
  },

  async getMoviesByStatus(status) {
    const response = await movieClient.get(`/movies/status/${status}`);
    return response.data;
  },

  async searchMovies(keyword) {
    const response = await movieClient.get(`/movies/search`, {
      params: { keyword },
    });
    return response.data;
  },

  async createMovie(movieData) {
    const response = await movieClient.post("/movies", movieData);
    return response.data;
  },

  async updateMovie(movieId, movieData) {
    const response = await movieClient.put(`/movies/${movieId}`, movieData);
    return response.data;
  },

  async updateMovieStatus(movieId, status) {
    const response = await movieClient.patch(
      `/movies/${movieId}/status`,
      null,
      {
        params: { status },
      },
    );
    return response.data;
  },
};
