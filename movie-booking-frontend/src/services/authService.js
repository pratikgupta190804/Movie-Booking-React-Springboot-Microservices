import axios from "axios";
import apiClient from "./apiClient";
import { API_CONFIG } from "../config/constants";

export const authService = {
  // Keycloak authentication
  async login(username, password) {
    try {
      const params = new URLSearchParams();
      params.append("grant_type", "password");
      params.append("client_id", API_CONFIG.KEYCLOAK_CLIENT_ID);
      params.append("client_secret", API_CONFIG.KEYCLOAK_CLIENT_SECRET);
      params.append("username", username);
      params.append("password", password);
      params.append("scope", "openid profile email");

      const response = await axios.post(
        `${API_CONFIG.KEYCLOAK_URL}/realms/${API_CONFIG.KEYCLOAK_REALM}/protocol/openid-connect/token`,
        params,
        {
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
          },
        },
      );
      console.log("Login response:", response.data);
      const { access_token, refresh_token } = response.data;
      localStorage.setItem("token", access_token);
      localStorage.setItem("refreshToken", refresh_token);

      // Get user info
      const userInfo = await this.getUserInfo(access_token);
      localStorage.setItem("user", JSON.stringify(userInfo));

      return { token: access_token, user: userInfo };
    } catch (error) {
      console.error("Login error:", error.response?.data || error.message);
      throw new Error(
        error.response?.data?.error_description || "Login failed",
      );
    }
  },

  async register(userData) {
    try {
      // Create user via user service registration endpoint
      const response = await apiClient.post("/v1/users/register", userData);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || "Registration failed");
    }
  },

  async getUserInfo(token) {
    try {
      const response = await axios.get(
        `${API_CONFIG.KEYCLOAK_URL}/realms/${API_CONFIG.KEYCLOAK_REALM}/protocol/openid-connect/userinfo`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      );
      return response.data;
    } catch (error) {
      throw new Error("Failed to fetch user info");
    }
  },

  async refreshToken() {
    try {
      const refreshToken = localStorage.getItem("refreshToken");
      if (!refreshToken) {
        throw new Error("No refresh token available");
      }

      const params = new URLSearchParams();
      params.append("grant_type", "refresh_token");
      params.append("client_id", API_CONFIG.KEYCLOAK_CLIENT_ID);
      params.append("client_secret", API_CONFIG.KEYCLOAK_CLIENT_SECRET);
      params.append("refresh_token", refreshToken);

      const response = await axios.post(
        `${API_CONFIG.KEYCLOAK_URL}/realms/${API_CONFIG.KEYCLOAK_REALM}/protocol/openid-connect/token`,
        params,
        {
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
          },
        },
      );

      const { access_token, refresh_token } = response.data;
      localStorage.setItem("token", access_token);
      if (refresh_token) {
        localStorage.setItem("refreshToken", refresh_token);
      }

      return access_token;
    } catch (error) {
      console.error("Token refresh failed:", error);
      // Clear tokens if refresh fails
      this.logout();
      throw error;
    }
  },

  logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("user");
  },

  getCurrentUser() {
    const userStr = localStorage.getItem("user");
    return userStr ? JSON.parse(userStr) : null;
  },

  getToken() {
    return localStorage.getItem("token");
  },

  isAuthenticated() {
    return !!this.getToken();
  },
};
