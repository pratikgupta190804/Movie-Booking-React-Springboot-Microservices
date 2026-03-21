// src/services/paymentService.js
import axios from "axios";
import { API_CONFIG } from "../config/constants";
import { setupInterceptors } from "./axiosInterceptors";

const paymentClient = axios.create({
  baseURL: API_CONFIG.PAYMENT_SERVICE, // http://localhost:8087/api
  headers: {
    "Content-Type": "application/json",
  },
});

setupInterceptors(paymentClient);

export const paymentService = {
  // ── Order ──────────────────────────────────────────────────────────────
  async createOrder(bookingId, amount, currency = "INR") {
    try {
      const payload = {
        bookingId,
        amount: parseFloat(amount), // ensure number not string
        currency,
        idempotencyKey: crypto.randomUUID(),
      };
      console.log("createOrder payload:", payload); // ← debug log
      const response = await paymentClient.post("/payments/order", payload);
      return response.data;
    } catch (err) {
      console.error("createOrder error response:", err.response?.data); // ← see exact validation error
      throw err;
    }
  },

  async verifyPayment(
    razorpayOrderId,
    razorpayPaymentId,
    razorpaySignature,
    bookingId,
  ) {
    const response = await paymentClient.post("/payments/verify", {
      razorpayOrderId,
      razorpayPaymentId,
      razorpaySignature,
      bookingId,
    });
    return response.data;
  },

  async cancelPayment(razorpayOrderId) {
    const response = await paymentClient.delete(`/payments/${razorpayOrderId}`);
    return response.data;
  },

  // ── Queries ────────────────────────────────────────────────────────────
  async getPaymentById(paymentId) {
    const response = await paymentClient.get(`/payments/${paymentId}`);
    return response.data;
  },

  async getPaymentByOrderId(orderId) {
    const response = await paymentClient.get(`/payments/order/${orderId}`);
    return response.data;
  },

  async getUserPayments() {
    const response = await paymentClient.get("/payments/user/history");
    return response.data;
  },

  // ── Refund ─────────────────────────────────────────────────────────────
  async refundPayment(paymentId, amount, reason) {
    const response = await paymentClient.post("/payments/refund", {
      paymentId,
      amount,
      reason,
    });
    return response.data;
  },

  async getRefundStatus(refundId) {
    const response = await paymentClient.get(`/payments/refund/${refundId}`);
    return response.data;
  },
};
