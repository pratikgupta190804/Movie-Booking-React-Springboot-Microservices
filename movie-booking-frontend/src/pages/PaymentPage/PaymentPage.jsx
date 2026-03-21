// src/pages/PaymentPage/PaymentPage.jsx
import { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { Clock, ShieldCheck } from "lucide-react";
import { useRazorpay } from "../../hooks/useRazorpay";

const PaymentPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { initiatePayment, loading } = useRazorpay();

  const { booking, userDetails } = location.state || {};
  const [paymentError, setPaymentError] = useState(null);

  // Guard — if someone navigates here directly without state
  if (!booking) {
    navigate("/", { replace: true });
    return null;
  }

  const handlePayment = async () => {
    setPaymentError(null);

    await initiatePayment({
      bookingId: booking.id,
      amount: booking.finalAmount,
      currency: "INR",
      userDetails,

      onSuccess: (verifyData) => {
        navigate("/booking/confirmation", {
          state: { paymentData: verifyData, booking },
          replace: true,
        });
      },

      onFailure: ({ message }) => {
        setPaymentError(message || "Payment failed. Please try again.");
      },

      onDismiss: () => {
        setPaymentError("Payment cancelled. You can retry below.");
      },
    });
  };

  return (
    <div className="min-h-screen bg-primary-50 py-8 px-4">
      <div className="max-w-lg mx-auto">

        {/* ── Header ──────────────────────────────────────────────── */}
        <h1 className="text-2xl font-bold text-center text-primary-900 mb-8">
          Complete Your Booking
        </h1>

        {/* ── Booking Summary Card ─────────────────────────────────── */}
        <div className="bg-white rounded-2xl p-6 mb-4 shadow-sm border
                        border-primary-200">
          <h2 className="text-lg font-semibold mb-4 text-primary-700 flex
                         items-center gap-2">
            <Clock className="w-5 h-5" />
            Booking Summary
          </h2>

          <div className="space-y-3 text-sm">
            <SummaryRow label="Movie"   value={booking.movieName} />
            <SummaryRow label="Theatre" value={booking.theatreName} />
            <SummaryRow label="Screen"  value={booking.screenName} />
            <SummaryRow
              label="Show Time"
              value={
                booking.showTime
                  ? new Date(booking.showTime).toLocaleString("en-IN", {
                      dateStyle: "medium",
                      timeStyle: "short",
                    })
                  : "—"
              }
            />
            <SummaryRow
              label="Seats"
              value={
                booking.seats
                  ?.map((s) => `${s.rowLabel || ""}${s.seatNumber}`)
                  .join(", ") || "—"
              }
            />

            <div className="border-t border-primary-100 pt-3 mt-3 space-y-2">
              <SummaryRow
                label="Subtotal"
                value={`₹${booking.totalAmount ?? "—"}`}
              />
              <SummaryRow
                label="Convenience Fee"
                value={`₹${booking.convenienceFee ?? "0"}`}
              />
              <SummaryRow
                label="Tax"
                value={`₹${booking.totalTax ?? "0"}`}
              />
            </div>

            <div className="border-t border-primary-200 pt-3 mt-3 flex
                            justify-between text-base font-bold text-primary-900">
              <span>Total Payable</span>
              <span className="text-indigo-600">₹{booking.finalAmount}</span>
            </div>
          </div>
        </div>

        {/* ── Expiry Timer ─────────────────────────────────────────── */}
        {booking.expiryTime && (
          <BookingExpiryTimer
            expiryTime={booking.expiryTime}
            onExpire={() =>
              navigate(`/shows/${booking.showId}/seats`, { replace: true })
            }
          />
        )}

        {/* ── Error Message ────────────────────────────────────────── */}
        {paymentError && (
          <div className="bg-red-50 border border-red-300 text-red-700
                          rounded-xl px-4 py-3 mb-4 text-sm text-center">
            {paymentError}
          </div>
        )}

        {/* ── Pay Button ───────────────────────────────────────────── */}
        <button
          onClick={handlePayment}
          disabled={loading}
          className="w-full bg-indigo-600 hover:bg-indigo-700
                     disabled:bg-indigo-300 disabled:cursor-not-allowed
                     text-white font-semibold py-4 rounded-xl
                     transition-all duration-200 flex items-center
                     justify-center gap-2 text-lg shadow-md"
        >
          {loading ? (
            <>
              <Spinner />
              Processing...
            </>
          ) : (
            `Pay ₹${booking.finalAmount}`
          )}
        </button>

        <div className="flex items-center justify-center gap-2 mt-4
                        text-primary-400 text-xs">
          <ShieldCheck className="w-4 h-4" />
          <span>Secured by Razorpay · 256-bit SSL encryption</span>
        </div>
      </div>
    </div>
  );
};

// ── Sub components ──────────────────────────────────────────────────────────

const SummaryRow = ({ label, value }) => (
  <div className="flex justify-between text-primary-700">
    <span className="text-primary-500">{label}</span>
    <span className="font-medium">{value}</span>
  </div>
);

const Spinner = () => (
  <svg className="animate-spin h-5 w-5" viewBox="0 0 24 24" fill="none">
    <circle
      className="opacity-25"
      cx="12" cy="12" r="10"
      stroke="currentColor" strokeWidth="4"
    />
    <path
      className="opacity-75"
      fill="currentColor"
      d="M4 12a8 8 0 018-8v8z"
    />
  </svg>
);

// ── Fixed: useEffect instead of useState for timer ──────────────────────────
const BookingExpiryTimer = ({ expiryTime, onExpire }) => {
  const [timeLeft, setTimeLeft] = useState("");

  useEffect(() => {                                     // ← was useState before
    const interval = setInterval(() => {
      const diff = new Date(expiryTime) - new Date();

      if (diff <= 0) {
        setTimeLeft("Expired");
        clearInterval(interval);
        onExpire?.();                                   // ← navigate away on expire
        return;
      }

      const mins = Math.floor(diff / 60000);
      const secs = Math.floor((diff % 60000) / 1000);
      setTimeLeft(`${mins}:${secs.toString().padStart(2, "0")}`);
    }, 1000);

    return () => clearInterval(interval);
  }, [expiryTime]);

  if (!timeLeft) return null;

  const isUrgent =
    timeLeft !== "Expired" && parseInt(timeLeft?.split(":")[0]) < 3;

  return (
    <div
      className={`rounded-xl px-4 py-3 mb-4 text-sm text-center font-medium
                  border ${
                    isUrgent
                      ? "bg-red-50 border-red-300 text-red-700"
                      : "bg-yellow-50 border-yellow-300 text-yellow-700"
                  }`}
    >
      {timeLeft === "Expired"
        ? "⚠️ Booking expired. Redirecting to seat selection..."
        : `⏱ Complete payment in ${timeLeft} to confirm your booking`}
    </div>
  );
};

export default PaymentPage;