import axios from "axios";
import { API_CONFIG } from "../config/constants";
import { setupInterceptors } from "./axiosInterceptors";

const inventoryClient = axios.create({
  baseURL: API_CONFIG.INVENTORY_SERVICE,
  headers: {
    "Content-Type": "application/json",
  },
});

// Setup auth token and refresh interceptors
setupInterceptors(inventoryClient);

export const inventoryService = {
  async getSeatMapForShow(showId) {
    const response = await inventoryClient.get(
      `/inventory/shows/${showId}/seats`,
    );
    return response.data;
  },

  async lockSeats(lockRequest) {
    const response = await inventoryClient.post(
      "/inventory/seats/lock",
      lockRequest,
    );
    return response.data;
  },

  async healthCheck() {
    const response = await inventoryClient.get("/inventory/health");
    return response.data;
  },
};
