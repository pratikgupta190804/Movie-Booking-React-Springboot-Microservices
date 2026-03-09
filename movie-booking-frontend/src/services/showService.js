import axios from "axios";
import { API_CONFIG } from "../config/constants";
import { setupInterceptors } from "./axiosInterceptors";

const showClient = axios.create({
  baseURL: API_CONFIG.SHOW_SERVICE,
  headers: {
    "Content-Type": "application/json",
  },
});

// Setup auth token and refresh interceptors
setupInterceptors(showClient);

export const showService = {
  async getAllShows() {
    const response = await showClient.get("/shows");
    return response.data;
  },

  async getShowById(showId) {
    const response = await showClient.get(`/shows/${showId}`);
    return response.data;
  },

  async getShowsByMovie(movieId) {
    const response = await showClient.get(`/shows/movie/${movieId}`);
    return response.data;
  },

  async getShowsByTheatre(theatreId) {
    const response = await showClient.get(`/shows/theatre/${theatreId}`);
    return response.data;
  },

  async getShowsByMovieAndCity(movieId, city) {
    const response = await showClient.get(
      `/shows/movie/${movieId}/city/${city}`,
    );
    return response.data;
  },

  async getShowsByDate(date) {
    const response = await showClient.get(`/shows/date/${date}`);
    return response.data;
  },
};
