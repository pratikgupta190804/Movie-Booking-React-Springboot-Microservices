import axios from "axios";
import { API_CONFIG } from "../config/constants";

const bookingClient = axios.create({
  baseURL: API_CONFIG.BOOKING_SERVICE,
  headers: {
    "Content-Type": "application/json",
  },
});

// Add auth token to requests
bookingClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

export const bookingService = {
  // Create a new booking
  async createBooking(bookingData) {
    try {
      const response = await bookingClient.post("/bookings", bookingData);
      return response.data;
    } catch (error) {
      console.error("Create booking error:", error);
      throw error;
    }
  },

  // Get booking by ID
  async getBookingById(bookingId) {
    try {
      // Get userId from token/localStorage
      const user = JSON.parse(localStorage.getItem("user") || "{}");
      const userId = user.sub || user.preferred_username || user.email;

      if (!userId) {
        throw new Error("User not authenticated");
      }

      const response = await bookingClient.get(`/bookings/${bookingId}`, {
        params: { userId: userId },
      });
      return response.data;
    } catch (error) {
      console.error("Get booking error:", error);
      throw error;
    }
  },

  // Get user's booking history
  async getUserBookings(userId) {
    try {
      const response = await bookingClient.get(`/bookings/user/${userId}`);
      return response.data;
    } catch (error) {
      console.error("Get user bookings error:", error);
      throw error;
    }
  },

  // Cancel a booking
  async cancelBooking(bookingId) {
    try {
      const response = await bookingClient.delete(`/bookings/${bookingId}`);
      return response.data;
    } catch (error) {
      console.error("Cancel booking error:", error);
      throw error;
    }
  },

  // Health check
  async healthCheck() {
    try {
      const response = await bookingClient.get("/bookings/health");
      return response.data;
    } catch (error) {
      console.error("Health check error:", error);
      throw error;
    }
  },
};
