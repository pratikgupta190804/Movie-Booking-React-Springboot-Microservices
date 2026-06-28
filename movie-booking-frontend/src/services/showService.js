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
    // Since there's no getAllShows endpoint, use date-range with a wide range
    const startDate = new Date("2000-01-01").toISOString().split(".")[0];
    const endDate = new Date("2099-12-31").toISOString().split(".")[0];
    try {
      const response = await showClient.get("/shows/date-range", {
        params: {
          startDate,
          endDate,
        },
      });
      return response.data || [];
    } catch (error) {
      console.warn("Could not fetch shows by date range:", error);
      return [];
    }
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
    return response.data || [];
  },

  async getShowsByMovieAndTheatre(movieId, theatreId, date = null) {
    const response = await showClient.get(
      `/shows/movie/${movieId}/theatre/${theatreId}`,
      {
        params: date ? { date } : {},
      },
    );
    return response.data || [];
  },

  async createShow(showData) {
    const response = await showClient.post("/shows", showData);
    return response.data;
  },

  async deleteShow(showId) {
    const response = await showClient.delete(`/shows/${showId}`);
    return response.data;
  },
};
