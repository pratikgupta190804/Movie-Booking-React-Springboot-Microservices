// src/hooks/useRazorpay.js
import { useState, useCallback } from "react";
import { paymentService } from "../services/paymentService";

export const useRazorpay = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState(null);

  const loadRazorpayScript = () => {
    return new Promise((resolve) => {
      if (window.Razorpay) {
        resolve(true);
        return;
      }
      const script  = document.createElement("script");
      script.src    = "https://checkout.razorpay.com/v1/checkout.js";
      script.onload = () => resolve(true);
      script.onerror = () => resolve(false);
      document.body.appendChild(script);
    });
  };

  const initiatePayment = useCallback(async ({
    bookingId,
    amount,
    currency = "INR",
    userDetails,
    onSuccess,
    onFailure,
    onDismiss,
  }) => {
    setLoading(true);
    setError(null);

    try {
      // ── Step 1: Load Razorpay script ──────────────────────────────
      const scriptLoaded = await loadRazorpayScript();
      if (!scriptLoaded) {
        throw new Error(
          "Failed to load Razorpay SDK. Check your internet connection."
        );
      }

      // ── Step 2: Create order on backend ───────────────────────────
      const orderData = await paymentService.createOrder(
        bookingId,
        amount,
        currency
      );

      console.log("Order created:", orderData); // ← temporary debug log

      // Validate response has required fields
      if (!orderData?.razorpayOrderId || !orderData?.razorpayKeyId) {
        throw new Error("Invalid payment order response from server.");
      }

      // ── Step 3: Open Razorpay checkout ────────────────────────────
      const options = {
        key:      orderData.razorpayKeyId,
        amount:   Math.round(parseFloat(orderData.amount) * 100), // rupees → paise
        currency: orderData.currency || currency,
        name:     "MovieBooking",
        description: `Booking ID: ${bookingId}`,
        order_id: orderData.razorpayOrderId,

        // ── On payment success ─────────────────────────────────────
        handler: async (razorpayResponse) => {
          try {
            const verifyData = await paymentService.verifyPayment(
              razorpayResponse.razorpay_order_id,
              razorpayResponse.razorpay_payment_id,
              razorpayResponse.razorpay_signature,
              bookingId
            );

            if (verifyData?.success) {
              onSuccess?.(verifyData);
            } else {
              onFailure?.({
                message: verifyData?.message || "Payment verification failed.",
              });
            }
          } catch (err) {
            console.error("Verification error:", err);
            onFailure?.({
              message:
                err.response?.data?.message ||
                "Payment verification failed. Please contact support.",
            });
          }
        },

        prefill: {
          name:    userDetails?.name  || "",
          email:   userDetails?.email || "",
          contact: userDetails?.phone || "",
        },

        theme: { color: "#6366F1" },

        modal: {
          ondismiss: async () => {
            try {
              await paymentService.cancelPayment(orderData.razorpayOrderId);
              console.info("Payment order cancelled after modal dismiss.");
            } catch (err) {
              console.warn("Could not cancel payment order:", err.message);
            }
            onDismiss?.();
          },
        },
      };

      const razorpay = new window.Razorpay(options);

      razorpay.on("payment.failed", (response) => {
        console.error("Razorpay payment failed:", response.error);
        onFailure?.({
          message: response.error?.description || "Payment failed.",
          code:    response.error?.code,
        });
      });

      razorpay.open();

    } catch (err) {
      const message =
        err.response?.data?.message ||
        err.message ||
        "Payment initiation failed. Please try again.";
      console.error("initiatePayment error:", err);
      setError(message);
      onFailure?.({ message });
    } finally {
      setLoading(false);
    }
  }, []);

  return { initiatePayment, loading, error };
};