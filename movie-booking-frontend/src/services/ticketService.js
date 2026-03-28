import axios from "axios";
import { API_CONFIG } from "../config/constants";
import { setupInterceptors } from "./axiosInterceptors";

const ticketClient = axios.create({
  baseURL: API_CONFIG.TICKET_SERVICE,
  headers: {
    "Content-Type": "application/json",
  },
});

// Setup auth token and refresh interceptors
setupInterceptors(ticketClient);
// Get ticket by bookingId
export const getTicketByBookingId = async (bookingId) => {
  const { data } = await ticketClient.get(`/tickets/booking/${bookingId}`);
  return data;
};

// Get all tickets for logged in user
export const getUserTickets = async () => {
  const { data } = await ticketClient.get("/tickets/my-tickets");
  return data;
};

// Get ticket by ticketId
export const getTicketById = async (ticketId) => {
  const { data } = await ticketClient.get(`/tickets/${ticketId}`);
  return data;
};
