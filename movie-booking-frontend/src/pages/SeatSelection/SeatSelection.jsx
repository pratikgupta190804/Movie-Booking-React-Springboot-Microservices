import { useState, useEffect, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Clock, AlertCircle, CheckCircle } from "lucide-react";
import { Card, CardBody, CardHeader } from "../../components/UI/Card";
import { Button } from "../../components/UI/Button";
import { Loader } from "../../components/UI/Loader";
import { Modal } from "../../components/UI/Modal";
import { SeatGrid, SeatLegend } from "../../components/SeatGrid/SeatGrid";
import { inventoryService } from "../../services/inventoryService";
import { showService } from "../../services/showService";
import { movieService } from "../../services/movieService";
import { theatreService } from "../../services/theatreService";
import { useAuth } from "../../context/AuthContext";
import { LOCK_DURATION_MINUTES } from "../../config/constants";
import toast from "react-hot-toast";

const SeatSelection = () => {
  const { showId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [show, setShow] = useState(null);
  const [movie, setMovie] = useState(null);
  const [theatre, setTheatre] = useState(null);
  const [seatMapData, setSeatMapData] = useState(null);
  const [seats, setSeats] = useState([]);
  const [selectedSeats, setSelectedSeats] = useState([]);
  const [loading, setLoading] = useState(true);
  const [locking, setLocking] = useState(false);
  const [lockData, setLockData] = useState(null);
  const [timeRemaining, setTimeRemaining] = useState(null);
  const [showConfirmModal, setShowConfirmModal] = useState(false);

  useEffect(() => {
    fetchShowAndSeats();
  }, [showId]);

  // Timer for locked seats
  useEffect(() => {
    if (!lockData) return;

    const interval = setInterval(() => {
      const now = new Date();
      const expiresAt = new Date(lockData.lockExpiresAt);
      const remaining = expiresAt - now;

      if (remaining <= 0) {
        toast.error("Seat lock expired! Please select seats again.");
        setLockData(null);
        setSelectedSeats([]);
        fetchShowAndSeats();
      } else {
        setTimeRemaining(Math.floor(remaining / 1000));
      }
    }, 1000);

    return () => clearInterval(interval);
  }, [lockData]);

  const fetchShowAndSeats = async () => {
    try {
      setLoading(true);

      // Fetch show data and seat map
      const [showData, seatMapData] = await Promise.all([
        showService.getShowById(showId),
        inventoryService.getSeatMapForShow(showId),
      ]);

      console.log("Fetched show data:", showData);
      console.log("Fetched seat map data:", seatMapData);
      console.log(
        "Seat prices from backend:",
        seatMapData.seats?.map((s) => ({
          id: s.seatId,
          type: s.seatType,
          price: s.price,
        })),
      );

      setShow(showData);
      setSeatMapData(seatMapData);

      // Fetch movie and theatre details
      const [movieData, theatreData] = await Promise.all([
        movieService.getMovieById(showData.movieId).catch((err) => {
          console.error("Error fetching movie:", err);
          return null;
        }),
        theatreService.getTheatreById(showData.theatreId).catch((err) => {
          console.error("Error fetching theatre:", err);
          return null;
        }),
      ]);

      console.log("Fetched movie data:", movieData);
      console.log("Fetched theatre data:", theatreData);

      setMovie(movieData);
      setTheatre(theatreData);

      // Map and normalize seats from backend response
      const normalizedSeats = (seatMapData.seats || [])
        .filter((seat) => seat.active !== false) // Only show active seats
        .map((seat) => ({
          seatId: seat.seatId,
          rowLabel: seat.rowLabel,
          seatNumber: seat.seatNumber,
          seatType: seat.seatType,
          status: seat.status,
          price: parseFloat(seat.price) || null, // Convert to number
          displayRow: seat.displayRow,
          displayColumn: seat.displayColumn,
        }))
        .sort((a, b) => {
          // Sort by display position or by row and seat number
          if (a.displayRow !== undefined && b.displayRow !== undefined) {
            if (a.displayRow === b.displayRow) {
              return (
                (a.displayColumn || a.seatNumber) -
                (b.displayColumn || b.seatNumber)
              );
            }
            return a.displayRow - b.displayRow;
          }
          // Fallback to row label and seat number
          if (a.rowLabel === b.rowLabel) {
            return a.seatNumber - b.seatNumber;
          }
          return a.rowLabel.localeCompare(b.rowLabel);
        });

      setSeats(normalizedSeats);
    } catch (error) {
      console.error("Error fetching show/seats:", error);
      toast.error("Failed to load seats");

      // Fallback: Show error - don't use demo data in production
      toast.error(
        "Unable to load show details. Please check if the show exists.",
      );
      setSeats([]);
    } finally {
      setLoading(false);
    }
  };

  const handleSeatClick = (seat) => {
    if (lockData) {
      toast.error(
        "Seats already locked! Proceed to payment or refresh to select again.",
      );
      return;
    }

    const isSelected = selectedSeats.some((s) => s.seatId === seat.seatId);

    if (isSelected) {
      setSelectedSeats((prev) => prev.filter((s) => s.seatId !== seat.seatId));
    } else {
      if (selectedSeats.length >= 10) {
        toast.error("You can select maximum 10 seats at a time");
        return;
      }
      console.log("Selected seat data:", seat);
      console.log("Seat price:", seat.price, "Type:", typeof seat.price);
      setSelectedSeats((prev) => [...prev, seat]);
    }
  };

  const handleLockSeats = async () => {
    if (selectedSeats.length === 0) {
      toast.error("Please select at least one seat");
      return;
    }

    setShowConfirmModal(true);
  };

  const confirmLockSeats = async () => {
    try {
      setLocking(true);
      setShowConfirmModal(false);
      // Get user ID from authenticated user
      const userId = user?.sub || user?.preferred_username || user?.email;

      if (!userId) {
        toast.error("User not authenticated. Please log in again.");
        navigate("/login");
        return;
      }

      const lockRequest = {
        showId: showId,
        userId: userId,
        seatIds: selectedSeats.map((s) => s.seatId),
      };

      const response = await inventoryService.lockSeats(lockRequest);

      setLockData(response);
      toast.success(
        "Seats locked successfully! Complete your booking within 5 minutes.",
      );

      // Refresh seat map to show locked seats
      await fetchShowAndSeats();
    } catch (error) {
      console.error("Error locking seats:", error);
      toast.error(
        error.response?.data?.message ||
          "Failed to lock seats. Please try again.",
      );
      setSelectedSeats([]);
    } finally {
      setLocking(false);
    }
  };

  const handleProceedToPayment = () => {
    // In production, integrate with payment gateway
    // For now, navigate to confirmation
    navigate(`/booking/confirmation/${showId}`, {
      state: {
        show,
        movie,
        theatre,
        screenName: seatMapData?.screenName,
        seats: selectedSeats,
        lockData,
        totalAmount,
      },
    });
  };

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, "0")}`;
  };

  const totalAmount = selectedSeats.reduce((sum, seat) => {
    // Use seat's individual price if available, otherwise fallback to show price
    const seatPrice =
      seat.price ||
      show?.seatPrices?.find((p) => p.seatType === seat.seatType)?.price ||
      show?.price ||
      200;
    return sum + parseFloat(seatPrice);
  }, 0);

  if (loading) {
    return <Loader fullScreen />;
  }

  return (
    <div className="min-h-screen bg-primary-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Show Info Header */}
        <Card className="mb-6">
          <CardHeader>
            <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
              <div>
                <h1 className="text-2xl font-bold text-primary-900 mb-2">
                  {movie?.title || "Loading..."}
                </h1>
                <div className="text-primary-600 space-y-1">
                  <p>
                    {theatre?.name || "Loading..."} |{" "}
                    {seatMapData?.screenName || "Loading..."}
                  </p>
                  <div className="flex items-center gap-2">
                    <Clock className="h-4 w-4" />
                    <span>
                      {show?.startTime
                        ? new Date(show.startTime).toLocaleString("en-US", {
                            weekday: "short",
                            month: "short",
                            day: "numeric",
                            hour: "2-digit",
                            minute: "2-digit",
                          })
                        : "Show Time"}
                    </span>
                  </div>
                </div>
              </div>

              {lockData && timeRemaining !== null && (
                <div className="bg-yellow-50 border-2 border-yellow-400 rounded-lg px-6 py-3">
                  <div className="flex items-center gap-2 text-yellow-800">
                    <AlertCircle className="h-5 w-5" />
                    <div>
                      <p className="text-sm font-medium">Seats Locked</p>
                      <p className="text-2xl font-bold">
                        {formatTime(timeRemaining)}
                      </p>
                    </div>
                  </div>
                </div>
              )}
            </div>
          </CardHeader>
        </Card>

        <div className="grid lg:grid-cols-3 gap-6">
          {/* Seat Map */}
          <div className="lg:col-span-2">
            <Card>
              <CardBody>
                {/* Screen */}
                <div className="mb-8">
                  <div className="bg-primary-200 text-center py-2 rounded-t-3xl">
                    <span className="text-sm font-semibold text-primary-700">
                      SCREEN
                    </span>
                  </div>
                </div>

                {/* Seats */}
                <div className="overflow-x-auto">
                  <SeatGrid
                    seats={seats}
                    selectedSeats={selectedSeats}
                    onSeatClick={handleSeatClick}
                    readOnly={!!lockData}
                  />
                </div>

                {/* Legend */}
                <SeatLegend seats={seats} />
              </CardBody>
            </Card>
          </div>

          {/* Booking Summary */}
          <div className="lg:col-span-1">
            <Card className="sticky top-24">
              <CardHeader>
                <h3 className="text-xl font-bold text-primary-900">
                  Booking Summary
                </h3>
              </CardHeader>
              <CardBody>
                <div className="space-y-4">
                  {selectedSeats.length === 0 ? (
                    <p className="text-primary-600 text-center py-8">
                      No seats selected
                    </p>
                  ) : (
                    <>
                      <div>
                        <p className="text-sm font-medium text-primary-700 mb-2">
                          Selected Seats ({selectedSeats.length})
                        </p>
                        <div className="flex flex-wrap gap-2">
                          {selectedSeats.map((seat) => (
                            <span
                              key={seat.seatId}
                              className="px-3 py-1 bg-green-100 text-green-800 rounded-full text-sm font-medium"
                            >
                              {seat.rowLabel}
                              {seat.seatNumber}
                            </span>
                          ))}
                        </div>
                      </div>

                      <div className="border-t border-primary-200 pt-4">
                        <div className="space-y-2 mb-3">
                          {selectedSeats.map((seat) => {
                            const seatPrice = parseFloat(
                              seat.price || show?.price || 200,
                            );
                            return (
                              <div
                                key={seat.seatId}
                                className="flex justify-between items-center text-sm p-2 bg-primary-50 rounded"
                              >
                                <div>
                                  <span className="font-semibold text-primary-900">
                                    {seat.rowLabel}
                                    {seat.seatNumber}
                                  </span>
                                  <span className="text-xs text-primary-600 ml-2">
                                    {seat.seatType}
                                  </span>
                                </div>
                                <span className="font-bold text-primary-900">
                                  ₹{seatPrice.toFixed(0)}
                                </span>
                              </div>
                            );
                          })}
                        </div>
                        <div className="flex justify-between text-lg font-bold text-primary-900">
                          <span>Total</span>
                          <span>₹{totalAmount.toFixed(2)}</span>
                        </div>
                      </div>

                      {lockData ? (
                        <Button
                          variant="primary"
                          size="lg"
                          className="w-full"
                          onClick={handleProceedToPayment}
                        >
                          Proceed to Payment
                        </Button>
                      ) : (
                        <Button
                          variant="primary"
                          size="lg"
                          className="w-full"
                          onClick={handleLockSeats}
                          loading={locking}
                        >
                          Lock Seats
                        </Button>
                      )}

                      <p className="text-xs text-primary-500 text-center">
                        {lockData
                          ? "Complete payment before timer expires"
                          : `Seats will be locked for ${LOCK_DURATION_MINUTES} minutes after confirmation`}
                      </p>
                    </>
                  )}
                </div>
              </CardBody>
            </Card>
          </div>
        </div>
      </div>

      {/* Confirmation Modal */}
      <Modal
        isOpen={showConfirmModal}
        onClose={() => setShowConfirmModal(false)}
        title="Confirm Seat Selection"
        size="md"
      >
        <div className="space-y-4">
          <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
            <div className="flex items-start gap-3">
              <AlertCircle className="h-5 w-5 text-blue-600 mt-0.5" />
              <div className="text-sm text-blue-800">
                <p className="font-medium mb-1">Important:</p>
                <ul className="list-disc list-inside space-y-1">
                  <li>
                    Seats will be locked for {LOCK_DURATION_MINUTES} minutes
                  </li>
                  <li>You must complete payment within this time</li>
                  <li>Seats will be released if payment is not completed</li>
                </ul>
              </div>
            </div>
          </div>

          <div>
            <p className="text-sm font-medium text-primary-700 mb-2">
              Selected Seats:
            </p>
            <div className="flex flex-wrap gap-2">
              {selectedSeats.map((seat) => (
                <span
                  key={seat.seatId}
                  className="px-3 py-1 bg-primary-100 text-primary-800 rounded-full text-sm"
                >
                  {seat.rowLabel}
                  {seat.seatNumber}
                </span>
              ))}
            </div>
          </div>

          <div className="flex gap-3 pt-4">
            <Button
              variant="outline"
              className="flex-1"
              onClick={() => setShowConfirmModal(false)}
            >
              Cancel
            </Button>
            <Button
              variant="primary"
              className="flex-1"
              onClick={confirmLockSeats}
              loading={locking}
            >
              Confirm & Lock
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default SeatSelection;
