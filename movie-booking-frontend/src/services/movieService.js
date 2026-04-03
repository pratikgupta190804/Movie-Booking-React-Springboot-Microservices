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
    // Get all active movies using status endpoint since getAllMovies doesn't exist
    const response = await movieClient.get("/movies/status/NOW_SHOWING");
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

  // Genre endpoints
  async getAllGenres() {
    const response = await movieClient.get("/genres");
    return response.data;
  },

  async getGenreById(genreId) {
    const response = await movieClient.get(`/genres/${genreId}`);
    return response.data;
  },

  async createGenre(genreName) {
    const response = await movieClient.post("/genres", { name: genreName });
    return response.data;
  },

  // Actor endpoints
  async getAllActors() {
    const response = await movieClient.get("/actors");
    return response.data;
  },

  async getActorById(actorId) {
    const response = await movieClient.get(`/actors/${actorId}`);
    return response.data;
  },

  async createActor(actorData) {
    const response = await movieClient.post("/actors", actorData);
    return response.data;
  },
};
