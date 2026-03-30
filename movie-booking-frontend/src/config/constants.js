// src/config/constants.js
// src/config/constants.js
export const API_CONFIG = {
  BASE_URL:          import.meta.env.VITE_GATEWAY_URL,
  MOVIES_SERVICE:    import.meta.env.VITE_GATEWAY_URL,
  USER_SERVICE:      import.meta.env.VITE_GATEWAY_URL,
  THEATRE_SERVICE:   import.meta.env.VITE_GATEWAY_URL,
  SHOW_SERVICE:      import.meta.env.VITE_GATEWAY_URL,
  INVENTORY_SERVICE: import.meta.env.VITE_GATEWAY_URL,
  BOOKING_SERVICE:   import.meta.env.VITE_GATEWAY_URL,
  PAYMENT_SERVICE:   import.meta.env.VITE_GATEWAY_URL,
  TICKET_SERVICE:    import.meta.env.VITE_GATEWAY_URL,

  KEYCLOAK_URL:           import.meta.env.VITE_KEYCLOAK_URL,
  KEYCLOAK_REALM:         import.meta.env.VITE_KEYCLOAK_REALM,
  KEYCLOAK_CLIENT_ID:     import.meta.env.VITE_KEYCLOAK_CLIENT_ID,
  KEYCLOAK_CLIENT_SECRET: import.meta.env.VITE_KEYCLOAK_CLIENT_SECRET,
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