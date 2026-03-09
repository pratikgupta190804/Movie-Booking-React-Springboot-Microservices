export const API_CONFIG = {
  BASE_URL: "http://localhost:8080/api",
  MOVIES_SERVICE: "http://localhost:8082/api",
  USER_SERVICE: "http://localhost:8081/api",
  THEATRE_SERVICE: "http://localhost:8083/api",
  SHOW_SERVICE: "http://localhost:8084/api",
  INVENTORY_SERVICE: "http://localhost:8085/api",
  KEYCLOAK_URL: "http://localhost:8181",
  KEYCLOAK_REALM: "movie-booking-app",
  KEYCLOAK_CLIENT_ID: "movie-booking-client",
  KEYCLOAK_CLIENT_SECRET: "RQBkKZJrfMeKZG9TNpao6AWNG7KZAEo9"
};

export const SEAT_STATUS = {
  AVAILABLE: "AVAILABLE",
  LOCKED: "LOCKED",
  BOOKED: "BOOKED",
};

export const SEAT_TYPES = {
  REGULAR: "REGULAR",
  PREMIUM: "PREMIUM",
  RECLINER: "RECLINER",
};

export const BOOKING_STATUS = {
  PENDING: "PENDING",
  CONFIRMED: "CONFIRMED",
  CANCELLED: "CANCELLED",
};

export const LOCK_DURATION_MINUTES = 5;
