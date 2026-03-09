import axios from "axios";
import { API_CONFIG } from "../config/constants";
import { setupInterceptors } from "./axiosInterceptors";

const theatreClient = axios.create({
  baseURL: API_CONFIG.THEATRE_SERVICE,
  headers: {
    "Content-Type": "application/json",
  },
});

// Setup auth token and refresh interceptors
setupInterceptors(theatreClient);

export const theatreService = {
  async getAllTheatres() {
    const response = await theatreClient.get("/theatres");
    return response.data;
  },

  async getTheatreById(theatreId) {
    const response = await theatreClient.get(`/theatres/${theatreId}`);
    return response.data;
  },

  async getTheatresByCity(city) {
    const response = await theatreClient.get(`/theatres/city/${city}`);
    return response.data;
  },

  async getScreensByTheatre(theatreId) {
    const response = await theatreClient.get(`/theatres/${theatreId}/screens`);
    return response.data;
  },

  async getScreenById(screenId) {
    const response = await theatreClient.get(`/screens/${screenId}`);
    return response.data;
  },
};
