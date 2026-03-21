// src/pages/BookingConfirmation/BookingConfirmation.jsx
import { useLocation, useNavigate } from "react-router-dom";
import { CheckCircle } from "lucide-react";

const BookingConfirmation = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { paymentData, booking } = location.state || {};

  if (!paymentData || !booking) {
    navigate("/", { replace: true });
    return null;
  }

  return (
    <div className="min-h-screen bg-primary-50 flex items-center
                    justify-center px-4 py-8">
      <div className="w-full max-w-lg text-center">

        {/* ── Success Icon ─────────────────────────────────────────── */}
        <div className="flex justify-center mb-6">
          <div className="bg-green-100 rounded-full p-4">
            <CheckCircle className="text-green-500 w-16 h-16" />
          </div>
        </div>

        <h1 className="text-3xl font-bold text-primary-900 mb-2">
          Booking Confirmed!
        </h1>
        <p className="text-primary-500 mb-8">
          Your payment was successful and seats are reserved.
        </p>

        {/* ── Confirmation Card ────────────────────────────────────── */}
        <div className="bg-white rounded-2xl p-6 border border-primary-200
                        shadow-sm text-left space-y-3 mb-6">

          <ConfirmRow
            label="Booking Ref"
            value={booking.bookingReference}
            highlight
          />
          <ConfirmRow label="Movie"   value={booking.movieName} />
          <ConfirmRow label="Theatre" value={booking.theatreName} />
          <ConfirmRow label="Screen"  value={booking.screenName} />
          <ConfirmRow
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
          <ConfirmRow
            label="Seats"
            value={
              booking.seats
                ?.map((s) => `${s.rowLabel || ""}${s.seatNumber}`)
                .join(", ") || "—"
            }
          />

          <div className="border-t border-primary-100 pt-3 space-y-2">
            <ConfirmRow
              label="Amount Paid"
              value={`₹${booking.finalAmount}`}
              highlight
            />
            <ConfirmRow
              label="Payment ID"
              value={paymentData.paymentId}
            />
          </div>
        </div>

        {/* ── Actions ──────────────────────────────────────────────── */}
        <div className="flex flex-col gap-3">
          <button
            onClick={() => navigate("/payment/history")}
            className="w-full bg-indigo-600 hover:bg-indigo-700 text-white
                       font-semibold py-3 rounded-xl transition-all duration-200"
          >
            View My Bookings
          </button>
          <button
            onClick={() => navigate("/")}
            className="w-full bg-primary-100 hover:bg-primary-200
                       text-primary-700 font-semibold py-3 rounded-xl
                       transition-all duration-200"
          >
            Back to Home
          </button>
        </div>
      </div>
    </div>
  );
};

const ConfirmRow = ({ label, value, highlight }) => (
  <div className="flex justify-between text-sm">
    <span className="text-primary-400">{label}</span>
    <span
      className={
        highlight
          ? "text-indigo-600 font-semibold"
          : "text-primary-800 font-medium"
      }
    >
      {value}
    </span>
  </div>
);

export default BookingConfirmation;