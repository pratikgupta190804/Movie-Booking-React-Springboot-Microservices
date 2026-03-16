import { useEffect, useState } from "react";
import { useParams, useLocation, Link } from "react-router-dom";
import { CheckCircle, Download, Calendar, MapPin, Clock } from "lucide-react";
import { Card, CardBody } from "../../components/UI/Card";
import { Button } from "../../components/UI/Button";
import { Loader } from "../../components/UI/Loader";
import { bookingService } from "../../services/bookingService";
import { movieService } from "../../services/movieService";
import { showService } from "../../services/showService";
import { theatreService } from "../../services/theatreService";
import toast from "react-hot-toast";

const BookingConfirmation = () => {
  const { bookingId } = useParams();
  const location = useLocation();
  const [booking, setBooking] = useState(null);
  const [movie, setMovie] = useState(null);
  const [show, setShow] = useState(null);
  const [theatre, setTheatre] = useState(null);
  const [loading, setLoading] = useState(true);

  // Get data from location state if available (for immediate display)
  const stateData = location.state || {};

  useEffect(() => {
    if (bookingId) {
      fetchBookingDetails();
    } else {
      setLoading(false);
      toast.error("No booking ID provided");
    }
  }, [bookingId]);

  const fetchBookingDetails = async () => {
    try {
      setLoading(true);

      // Validate booking ID
      if (!bookingId || bookingId === "undefined") {
        throw new Error("Invalid booking ID");
      }

      // Fetch booking details from backend
      const bookingData = await bookingService.getBookingById(bookingId);
      setBooking(bookingData);

      // Fetch related data (movie, show, theatre)
      const [movieData, showData, theatreData] = await Promise.all([
        movieService.getMovieById(bookingData.movieId).catch((err) => {
          console.error("Error fetching movie:", err);
          return stateData.movie || null;
        }),
        showService.getShowById(bookingData.showId).catch((err) => {
          console.error("Error fetching show:", err);
          return stateData.show || null;
        }),
        theatreService.getTheatreById(bookingData.theatreId).catch((err) => {
          console.error("Error fetching theatre:", err);
          return stateData.theatre || null;
        }),
      ]);

      setMovie(movieData);
      setShow(showData);
      setTheatre(theatreData);
    } catch (error) {
      console.error("Error fetching booking details:", error);
      toast.error("Failed to load booking details");

      // Fallback to state data if available
      if (stateData.bookingId) {
        setBooking({
          id: stateData.bookingId,
          bookingReference: stateData.bookingReference,
          totalAmount: stateData.totalAmount,
          seats: stateData.seats,
          status: "PENDING",
        });
        setMovie(stateData.movie);
        setShow(stateData.show);
        setTheatre(stateData.theatre);
      }
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <Loader fullScreen />;
  }

  if (!booking) {
    return (
      <div className="min-h-screen bg-primary-50 flex items-center justify-center">
        <Card>
          <CardBody className="text-center py-12">
            <p className="text-xl text-primary-900 mb-4">Booking not found</p>
            <Link to="/">
              <Button variant="primary">Back to Home</Button>
            </Link>
          </CardBody>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-primary-50 py-12">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Success Message */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-20 h-20 bg-green-100 rounded-full mb-4">
            <CheckCircle className="h-12 w-12 text-green-600" />
          </div>
          <h1 className="text-4xl font-bold text-primary-900 mb-2">
            Booking Confirmed!
          </h1>
          <p className="text-lg text-primary-600">
            Your tickets have been booked successfully
          </p>
        </div>

        {/* Booking Details */}
        <Card className="mb-6">
          <CardBody>
            <div className="border-b border-primary-200 pb-4 mb-4">
              <div className="flex justify-between items-start mb-2">
                <h2 className="text-2xl font-bold text-primary-900">
                  {movie?.title || "Movie Title"}
                </h2>
                <span
                  className={`px-4 py-1 rounded-full text-sm font-semibold ${
                    booking.status === "CONFIRMED"
                      ? "bg-green-100 text-green-800"
                      : booking.status === "PENDING"
                        ? "bg-yellow-100 text-yellow-800"
                        : "bg-red-100 text-red-800"
                  }`}
                >
                  {booking.status}
                </span>
              </div>
              <p className="text-primary-600">
                Booking Reference: {booking.bookingReference}
              </p>
              <p className="text-sm text-primary-500">
                Booking ID: {booking.id}
              </p>
            </div>

            <div className="space-y-4">
              {/* Theatre & Show Details */}
              <div className="grid md:grid-cols-2 gap-4">
                <div>
                  <div className="flex items-center gap-2 text-primary-600 mb-1">
                    <MapPin className="h-4 w-4" />
                    <span className="text-sm font-medium">Theatre</span>
                  </div>
                  <p className="font-semibold text-primary-900">
                    {theatre?.name || "Theatre Name"}
                  </p>
                  <p className="text-sm text-primary-600">
                    {stateData.screenName || "Screen 1"}
                  </p>
                </div>

                <div>
                  <div className="flex items-center gap-2 text-primary-600 mb-1">
                    <Calendar className="h-4 w-4" />
                    <span className="text-sm font-medium">Show Time</span>
                  </div>
                  <p className="font-semibold text-primary-900">
                    {show?.startTime
                      ? new Date(show.startTime).toLocaleDateString("en-US", {
                          weekday: "long",
                          month: "long",
                          day: "numeric",
                        })
                      : "Date"}
                  </p>
                  <div className="flex items-center gap-1 text-sm text-primary-600">
                    <Clock className="h-3 w-3" />
                    <span>
                      {show?.startTime
                        ? new Date(show.startTime).toLocaleTimeString("en-US", {
                            hour: "2-digit",
                            minute: "2-digit",
                          })
                        : "Time"}
                    </span>
                  </div>
                </div>
              </div>

              {/* Seats */}
              <div className="border-t border-primary-200 pt-4">
                <p className="text-sm font-medium text-primary-700 mb-2">
                  Seats ({booking.seats?.length || 0})
                </p>
                <div className="flex flex-wrap gap-2">
                  {booking.seats?.map((seat, index) => (
                    <span
                      key={seat.seatId || index}
                      className="px-4 py-2 bg-primary-100 text-primary-800 rounded-lg font-semibold"
                    >
                      {seat.seatNumber}
                    </span>
                  )) || <span className="text-primary-600">No seats</span>}
                </div>
              </div>

              {/* Payment Summary */}
              <div className="border-t border-primary-200 pt-4">
                <div className="space-y-2">
                  <div className="flex justify-between items-center text-sm">
                    <span className="text-primary-600">Subtotal</span>
                    <span className="font-medium text-primary-900">
                      ₹{booking.subtotal?.toFixed(2) || "0.00"}
                    </span>
                  </div>
                  <div className="flex justify-between items-center text-sm">
                    <span className="text-primary-600">Convenience Fee</span>
                    <span className="font-medium text-primary-900">
                      ₹{booking.convenienceFee?.toFixed(2) || "0.00"}
                    </span>
                  </div>
                  <div className="flex justify-between items-center text-sm">
                    <span className="text-primary-600">GST (18%)</span>
                    <span className="font-medium text-primary-900">
                      ₹{booking.gst?.toFixed(2) || "0.00"}
                    </span>
                  </div>
                  <div className="flex justify-between items-center pt-2 border-t border-primary-200">
                    <span className="text-lg font-medium text-primary-700">
                      Total Amount{" "}
                      {booking.status === "CONFIRMED" ? "Paid" : ""}
                    </span>
                    <span className="text-2xl font-bold text-primary-900">
                      ₹{booking.totalAmount?.toFixed(2) || "0.00"}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </CardBody>
        </Card>

        {/* Actions */}
        <div className="flex flex-col sm:flex-row gap-4">
          <Button
            variant="primary"
            size="lg"
            className="flex-1 flex items-center justify-center gap-2"
          >
            <Download className="h-5 w-5" />
            Download Ticket
          </Button>
          <Link to="/" className="flex-1">
            <Button variant="outline" size="lg" className="w-full">
              Back to Home
            </Button>
          </Link>
        </div>

        {/* Instructions */}
        <Card className="mt-6">
          <CardBody>
            <h3 className="font-semibold text-primary-900 mb-3">
              Important Instructions
            </h3>
            <ul className="space-y-2 text-sm text-primary-700">
              <li className="flex items-start gap-2">
                <span className="text-accent mt-0.5">•</span>
                <span>Please carry a valid ID proof to the theatre</span>
              </li>
              <li className="flex items-start gap-2">
                <span className="text-accent mt-0.5">•</span>
                <span>Arrive at least 15 minutes before show time</span>
              </li>
              <li className="flex items-start gap-2">
                <span className="text-accent mt-0.5">•</span>
                <span>
                  Show this confirmation or downloaded ticket at the counter
                </span>
              </li>
              <li className="flex items-start gap-2">
                <span className="text-accent mt-0.5">•</span>
                <span>Food and beverages are available at the theatre</span>
              </li>
            </ul>
          </CardBody>
        </Card>
      </div>
    </div>
  );
};

export default BookingConfirmation;
