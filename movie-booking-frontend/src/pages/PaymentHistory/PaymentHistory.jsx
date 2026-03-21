// src/pages/PaymentHistory/PaymentHistory.jsx
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { paymentService } from "../../services/paymentService";
import { Loader } from "../../components/UI/Loader";

const STATUS_STYLES = {
  SUCCESS:             "bg-green-100 text-green-700",
  CREATED:             "bg-blue-100 text-blue-700",
  PENDING:             "bg-yellow-100 text-yellow-700",
  FAILED:              "bg-red-100 text-red-700",
  CANCELLED:           "bg-gray-100 text-gray-600",
  REFUND_INITIATED:    "bg-orange-100 text-orange-700",
  PARTIALLY_REFUNDED:  "bg-purple-100 text-purple-700",
  FULLY_REFUNDED:      "bg-teal-100 text-teal-700",
};

const PaymentHistory = () => {
  const navigate = useNavigate();
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchPayments = async () => {
      try {
        setLoading(true);
        const data = await paymentService.getUserPayments();
        setPayments(data);
      } catch {
        setError("Failed to load payment history. Please try again.");
      } finally {
        setLoading(false);
      }
    };
    fetchPayments();
  }, []);

  if (loading) return <Loader fullScreen />;

  return (
    <div className="min-h-screen bg-primary-50 py-8 px-4">
      <div className="max-w-2xl mx-auto">

        <h1 className="text-2xl font-bold text-primary-900 mb-8">
          Payment History
        </h1>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-600
                          rounded-xl px-4 py-3 mb-6 text-sm text-center">
            {error}
          </div>
        )}

        {!error && payments.length === 0 && (
          <div className="bg-white rounded-2xl p-12 text-center border
                          border-primary-200 shadow-sm">
            <p className="text-primary-400 text-lg mb-4">No payments yet</p>
            <button
              onClick={() => navigate("/movies")}
              className="bg-indigo-600 hover:bg-indigo-700 text-white
                         font-semibold px-6 py-2 rounded-xl transition-all"
            >
              Book a Movie
            </button>
          </div>
        )}

        <div className="space-y-4">
          {payments.map((payment) => (
            <div
              key={payment.id}
              className="bg-white rounded-2xl p-5 border border-primary-200
                         shadow-sm"
            >
              {/* ── Top row ──────────────────────────────────────── */}
              <div className="flex justify-between items-start mb-4">
                <div>
                  <p className="text-xs text-primary-400 mb-1">Payment ID</p>
                  <p className="text-sm font-mono text-primary-700 truncate
                                max-w-[200px]">
                    {payment.id}
                  </p>
                </div>
                <span
                  className={`text-xs font-semibold px-3 py-1 rounded-full
                              ${STATUS_STYLES[payment.status] || "bg-gray-100 text-gray-600"}`}
                >
                  {payment.status}
                </span>
              </div>

              {/* ── Details grid ─────────────────────────────────── */}
              <div className="grid grid-cols-2 gap-3 text-sm">
                <DetailItem label="Amount"     value={`₹${payment.amount}`} />
                <DetailItem
                  label="Date"
                  value={new Date(payment.createdAt).toLocaleDateString(
                    "en-IN", { dateStyle: "medium" }
                  )}
                />
                <DetailItem label="Method"   value={payment.method   || "—"} />
                <DetailItem label="Provider" value={payment.provider || "—"} />
                <DetailItem
                  label="Booking ID"
                  value={payment.bookingId}
                  fullWidth
                />
              </div>

              {/* ── Failure reason ───────────────────────────────── */}
              {payment.failureReason && (
                <div className="mt-3 bg-red-50 border border-red-200
                                rounded-lg px-3 py-2 text-xs text-red-600">
                  ❌ {payment.failureReason}
                </div>
              )}

              {/* ── Refund info ──────────────────────────────────── */}
              {payment.refundedAmount > 0 && (
                <div className="mt-3 bg-orange-50 border border-orange-200
                                rounded-lg px-3 py-2 text-xs text-orange-700">
                  ↩ Refunded: ₹{payment.refundedAmount}
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

const DetailItem = ({ label, value, fullWidth }) => (
  <div className={fullWidth ? "col-span-2" : ""}>
    <p className="text-primary-400 text-xs mb-0.5">{label}</p>
    <p className="text-primary-800 font-medium truncate">{value}</p>
  </div>
);

export default PaymentHistory;