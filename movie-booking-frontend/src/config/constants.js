// src/config/constants.js
export const API_CONFIG = {
  BASE_URL:          "http://localhost:8080/api",
  MOVIES_SERVICE:    "http://localhost:8082/api",
  USER_SERVICE:      "http://localhost:8081/api",
  THEATRE_SERVICE:   "http://localhost:8083/api",
  SHOW_SERVICE:      "http://localhost:8084/api",
  INVENTORY_SERVICE: "http://localhost:8085/api",
  BOOKING_SERVICE:   "http://localhost:8086/api",
  PAYMENT_SERVICE:   "http://localhost:8087/api",
  KEYCLOAK_URL:      "http://localhost:8181",
  KEYCLOAK_REALM:    "movie-booking-app",
  KEYCLOAK_CLIENT_ID:     "movie-booking-client",
  KEYCLOAK_CLIENT_SECRET: "RQBkKZJrfMeKZG9TNpao6AWNG7KZAEo9",
};

export const SEAT_STATUS = {
  AVAILABLE: "AVAILABLE",
  LOCKED:    "LOCKED",
  BOOKED:    "BOOKED",
};

export const SEAT_TYPES = {
  REGULAR:  "REGULAR",
  PREMIUM:  "PREMIUM",
  RECLINER: "RECLINER",
};

export const BOOKING_STATUS = {
  PENDING:   "PENDING",
  CONFIRMED: "CONFIRMED",
  CANCELLED: "CANCELLED",
  EXPIRED:   "EXPIRED",
};

// ── Added ──────────────────────────────────────────────────────────────────
export const PAYMENT_STATUS = {
  CREATED:            "CREATED",
  PENDING:            "PENDING",
  SUCCESS:            "SUCCESS",
  FAILED:             "FAILED",
  CANCELLED:          "CANCELLED",
  REFUND_INITIATED:   "REFUND_INITIATED",
  PARTIALLY_REFUNDED: "PARTIALLY_REFUNDED",
  FULLY_REFUNDED:     "FULLY_REFUNDED",
};

export const LOCK_DURATION_MINUTES = 5;
export const MAX_SEATS_PER_BOOKING = 10;