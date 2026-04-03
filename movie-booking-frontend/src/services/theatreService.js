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
  async getAllTheatres(pageNumber = 0, pageSize = 100) {
    // Get all theatres using search endpoint with pagination since getAllTheatres doesn't exist
    const response = await theatreClient.get("/theatres/search", {
      params: {
        keyword: "",
        page: pageNumber,
        size: pageSize,
      },
    });
    // Return content array since it's paginated
    return response.data.content || response.data;
  },

  async getTheatreById(theatreId) {
    const response = await theatreClient.get(`/theatres/${theatreId}`);
    return response.data;
  },

  async getTheatresByCity(city) {
    const response = await theatreClient.get(`/theatres/city/${city}`);
    return response.data;
  },

  async getTheatresByBrand(brand) {
    const response = await theatreClient.get(`/theatres/brand/${brand}`);
    return response.data;
  },

  async searchTheatres(keyword) {
    const response = await theatreClient.get(`/theatres/search`, {
      params: { keyword },
    });
    return response.data;
  },

  async getTheatresNear(lat, lng, radius) {
    const response = await theatreClient.get(`/theatres/near`, {
      params: { lat, lng, radius },
    });
    return response.data;
  },

  async createTheatre(theatreData) {
    const response = await theatreClient.post("/theatres", theatreData);
    return response.data;
  },

  async updateTheatre(theatreId, theatreData) {
    const response = await theatreClient.put(
      `/theatres/${theatreId}`,
      theatreData,
    );
    return response.data;
  },

  async activateTheatre(theatreId) {
    const response = await theatreClient.patch(
      `/theatres/${theatreId}/activate`,
    );
    return response.data;
  },

  async deactivateTheatre(theatreId) {
    const response = await theatreClient.patch(
      `/theatres/${theatreId}/deactivate`,
    );
    return response.data;
  },

  async getScreensByTheatre(theatreId) {
    const response = await theatreClient.get(`/screens/theatre/${theatreId}`);
    return response.data;
  },

  async getScreenById(screenId) {
    const response = await theatreClient.get(`/screens/${screenId}`);
    return response.data;
  },

  async createScreen(theatreId, screenData) {
    const response = await theatreClient.post(
      `/screens/theatre/${theatreId}`,
      screenData,
    );
    return response.data;
  },

  async updateScreen(screenId, screenData) {
    const response = await theatreClient.put(
      `/screens/${screenId}`,
      screenData,
    );
    return response.data;
  },
};
