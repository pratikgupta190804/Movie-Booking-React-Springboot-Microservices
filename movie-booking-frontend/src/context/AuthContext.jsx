import { createContext, useContext, useState, useEffect, useMemo } from "react";
import { authService } from "../services/authService";
import toast from "react-hot-toast";
import { API_CONFIG } from "../config/constants";

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is logged in on mount
    const currentUser = authService.getCurrentUser();
    const token = authService.getToken();

    if (currentUser && token) {
      setUser(currentUser);
    }
    setLoading(false);
  }, []);

  const login = async (username, password) => {
    try {
      const { user: userData, token } = await authService.login(
        username,
        password,
      );
      setUser(userData);
      toast.success("Login successful!");
      return true;
    } catch (error) {
      toast.error(error.message || "Login failed");
      return false;
    }
  };

  const register = async (userData) => {
    try {
      await authService.register(userData);
      toast.success("Registration successful! Please login.");
      return true;
    } catch (error) {
      toast.error(error.message || "Registration failed");
      return false;
    }
  };

  const logout = () => {
    authService.logout();
    setUser(null);
    toast.success("Logged out successfully");
  };

  // In your AuthContext.jsx — add this function
  // AuthContext.jsx — replace handleOAuthCallback with this
  const handleOAuthCallback = async (code) => {
    try {
      const params = new URLSearchParams({
        grant_type: "authorization_code",
        client_id: API_CONFIG.KEYCLOAK_CLIENT_ID, // ← use constants
        client_secret: API_CONFIG.KEYCLOAK_CLIENT_SECRET,
        code: code,
        redirect_uri: "http://localhost:3000/auth/callback",
      });

      const response = await fetch(
        `${API_CONFIG.KEYCLOAK_URL}/realms/${API_CONFIG.KEYCLOAK_REALM}/protocol/openid-connect/token`,
        {
          method: "POST",
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
          body: params,
        },
      );

      if (!response.ok) {
        const errorBody = await response.json();
        console.error("Keycloak token error:", errorBody);
        throw new Error(errorBody.error_description || "Token exchange failed");
      }

      const tokens = await response.json();

      if (tokens.access_token) {
        // ── Store tokens exactly same way as authService.login() ───────
        localStorage.setItem("token", tokens.access_token); // ← "token" not "access_token"
        localStorage.setItem("refreshToken", tokens.refresh_token); // ← "refreshToken"

        // ── Fetch userInfo from Keycloak same way as authService.login()
        const userInfo = await authService.getUserInfo(tokens.access_token);
        localStorage.setItem("user", JSON.stringify(userInfo)); // ← store user too

        // ── Set user in context ────────────────────────────────────────
        setUser(userInfo);

        await authService.syncUserToDatabase(userInfo, tokens.access_token);

        toast.success("Logged in with Google!");
      } else {
        throw new Error("No access token received");
      }
    } catch (error) {
      console.error("OAuth callback error:", error);
      toast.error(error.message || "Google login failed");
      throw error;
    }
  };

  // Compute user roles from JWT token
  const userRoles = useMemo(() => {
    if (!user) return [];

    const token = authService.getToken();
    if (token) {
      try {
        const decoded = parseJwt(token);
        if (decoded.realm_access?.roles) {
          return decoded.realm_access.roles.map((role) => role.toUpperCase());
        }
      } catch (e) {
        console.warn("Failed to decode JWT token");
      }
    }

    // Fallback: try resource access if available
    if (user.resource_access) {
      const roles = [];
      Object.values(user.resource_access).forEach((resource) => {
        if (resource.roles) {
          roles.push(...resource.roles);
        }
      });
      return roles.map((role) => role.toUpperCase());
    }

    return [];
  }, [user]);

  const hasRole = (role) => {
    return userRoles.includes(role.toUpperCase());
  };

  const hasAnyRole = (roles) => {
    const roleArray = Array.isArray(roles) ? roles : [roles];
    return roleArray.some((role) => userRoles.includes(role.toUpperCase()));
  };

  const value = {
    user,
    loading,
    login,
    register,
    logout,
    handleOAuthCallback,
    isAuthenticated: !!user,
    userRoles,
    hasRole,
    hasAnyRole,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

const parseJwt = (token) => {
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join(""),
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    console.error("Failed to parse JWT:", e);
    return null;
  }
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};
