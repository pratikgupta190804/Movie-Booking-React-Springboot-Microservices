import { SEAT_STATUS } from "../../config/constants";

export const SeatGrid = ({
  seats,
  selectedSeats,
  onSeatClick,
  readOnly = false,
}) => {
  const getSeatColor = (seat) => {
    if (selectedSeats.some((s) => s.seatId === seat.seatId)) {
      return "bg-green-500 text-white border-green-600";
    }

    // Color based on status and seat type
    switch (seat.status) {
      case SEAT_STATUS.AVAILABLE:
        // Different colors for different seat types
        if (seat.seatType === "PREMIUM" || seat.seatType === "RECLINER") {
          return "bg-purple-50 border-purple-400 hover:bg-purple-100 cursor-pointer";
        } else if (seat.seatType === "VIP") {
          return "bg-amber-50 border-amber-400 hover:bg-amber-100 cursor-pointer";
        }
        return "bg-white border-primary-300 hover:bg-primary-50 cursor-pointer";
      case SEAT_STATUS.LOCKED:
        return "bg-yellow-100 border-yellow-300 cursor-not-allowed";
      case SEAT_STATUS.BOOKED:
        return "bg-primary-200 border-primary-300 cursor-not-allowed";
      default:
        return "bg-primary-100 border-primary-200 cursor-not-allowed";
    }
  };

  const handleSeatClick = (seat) => {
    if (readOnly || seat.status !== SEAT_STATUS.AVAILABLE) return;
    onSeatClick(seat);
  };

  // Group seats by row
  const seatsByRow = seats.reduce((acc, seat) => {
    const rowKey = seat.rowLabel || seat.displayRow || "Unknown";
    if (!acc[rowKey]) {
      acc[rowKey] = [];
    }
    acc[rowKey].push(seat);
    return acc;
  }, {});

  // Sort rows
  const sortedRows = Object.keys(seatsByRow).sort();

  return (
    <div className="space-y-2">
      {sortedRows.map((rowLabel) => {
        const rowSeats = seatsByRow[rowLabel].sort(
          (a, b) => a.seatNumber - b.seatNumber,
        );

        // Find the max displayColumn to know how many positions we need
        const maxColumn = Math.max(
          ...rowSeats.map((s) => s.displayColumn || s.seatNumber - 1),
        );

        // Create an array with all positions, filling gaps with null
        const seatPositions = new Array(maxColumn + 1).fill(null);
        rowSeats.forEach((seat) => {
          const position =
            seat.displayColumn !== undefined
              ? seat.displayColumn
              : seat.seatNumber - 1;
          seatPositions[position] = seat;
        });

        return (
          <div key={rowLabel} className="flex items-center gap-2">
            <div className="w-8 text-center font-semibold text-primary-600">
              {rowLabel}
            </div>
            <div className="flex gap-2">
              {seatPositions.map((seat, index) => {
                if (seat === null) {
                  // Empty space for skipped seats (stairs/aisles)
                  return <div key={`empty-${index}`} className="w-10 h-10" />;
                }

                return (
                  <button
                    key={seat.seatId}
                    onClick={() => handleSeatClick(seat)}
                    disabled={readOnly || seat.status !== SEAT_STATUS.AVAILABLE}
                    className={`
                      w-10 h-10 rounded border-2 text-xs font-medium
                      transition-all duration-200
                      ${getSeatColor(seat)}
                    `}
                    title={`Row ${seat.rowLabel}, Seat ${seat.seatNumber}\nType: ${seat.seatType}\nPrice: ₹${seat.price || "N/A"}\nStatus: ${seat.status}`}
                  >
                    {seat.seatNumber}
                  </button>
                );
              })}
            </div>
          </div>
        );
      })}
    </div>
  );
};

export const SeatLegend = ({ seats = [] }) => {
  // Get unique seat types with their prices
  const seatTypes = seats.reduce((acc, seat) => {
    if (!acc[seat.seatType]) {
      acc[seat.seatType] = seat.price;
    }
    return acc;
  }, {});

  return (
    <div className="space-y-4">
      {/* Seat Type Pricing */}
      {Object.keys(seatTypes).length > 0 && (
        <div className="border-b border-primary-200 pb-4">
          <p className="text-sm font-semibold text-primary-700 mb-3">
            Seat Types & Prices
          </p>
          <div className="grid grid-cols-2 gap-3">
            {Object.entries(seatTypes).map(([type, price]) => (
              <div
                key={type}
                className="flex items-center justify-between gap-2 text-sm"
              >
                <div className="flex items-center gap-2">
                  <div
                    className={`w-6 h-6 rounded border-2 ${
                      type === "PREMIUM" || type === "RECLINER"
                        ? "bg-purple-50 border-purple-400"
                        : type === "VIP"
                          ? "bg-amber-50 border-amber-400"
                          : "bg-white border-primary-300"
                    }`}
                  />
                  <span className="text-primary-700 font-medium">{type}</span>
                </div>
                <span className="text-primary-900 font-semibold">
                  ₹{parseFloat(price || 0).toFixed(0)}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Status Legend */}
      <div>
        <p className="text-sm font-semibold text-primary-700 mb-3">
          Seat Status
        </p>
        <div className="flex flex-wrap gap-4">
          <div className="flex items-center gap-2">
            <div className="w-6 h-6 rounded border-2 bg-white border-primary-300" />
            <span className="text-sm text-primary-700">Available</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-6 h-6 rounded border-2 bg-green-500 border-green-600" />
            <span className="text-sm text-primary-700">Selected</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-6 h-6 rounded border-2 bg-yellow-100 border-yellow-300" />
            <span className="text-sm text-primary-700">Locked</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-6 h-6 rounded border-2 bg-primary-200 border-primary-300" />
            <span className="text-sm text-primary-700">Booked</span>
          </div>
        </div>
      </div>
    </div>
  );
};
