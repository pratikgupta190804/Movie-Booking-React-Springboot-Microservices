import axios from "axios";
import { API_CONFIG } from "../config/constants";
import { setupInterceptors } from "./axiosInterceptors";

const apiClient = axios.create({
  baseURL: API_CONFIG.USER_SERVICE,
  headers: {
    "Content-Type": "application/json",
  },
});

// Setup auth token and refresh interceptors
setupInterceptors(apiClient);

export default apiClient;
