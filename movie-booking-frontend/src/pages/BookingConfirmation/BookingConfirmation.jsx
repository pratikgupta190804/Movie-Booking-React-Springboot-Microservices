import { useEffect } from "react";
import { useParams, useLocation, Link } from "react-router-dom";
import { CheckCircle, Download, Calendar, MapPin, Clock } from "lucide-react";
import { Card, CardBody } from "../../components/UI/Card";
import { Button } from "../../components/UI/Button";

const BookingConfirmation = () => {
  const { bookingId } = useParams();
  const location = useLocation();
  const { show, seats, lockData, totalAmount } = location.state || {};

  useEffect(() => {
    // In production, you would send payment success event to backend via Kafka
    // This would trigger the confirmSeatsForBooking in inventory service
    console.log("Booking confirmed:", { bookingId, lockData });
  }, [bookingId]);

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
                  {show?.movieTitle || "Movie Title"}
                </h2>
                <span className="px-4 py-1 bg-green-100 text-green-800 rounded-full text-sm font-semibold">
                  CONFIRMED
                </span>
              </div>
              <p className="text-primary-600">Booking ID: {bookingId}</p>
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
                    {show?.theatreName || "Theatre Name"}
                  </p>
                  <p className="text-sm text-primary-600">
                    {show?.screenName || "Screen 1"}
                  </p>
                </div>

                <div>
                  <div className="flex items-center gap-2 text-primary-600 mb-1">
                    <Calendar className="h-4 w-4" />
                    <span className="text-sm font-medium">Show Time</span>
                  </div>
                  <p className="font-semibold text-primary-900">
                    {show?.showTime
                      ? new Date(show.showTime).toLocaleDateString("en-US", {
                          weekday: "long",
                          month: "long",
                          day: "numeric",
                        })
                      : "Date"}
                  </p>
                  <div className="flex items-center gap-1 text-sm text-primary-600">
                    <Clock className="h-3 w-3" />
                    <span>
                      {show?.showTime
                        ? new Date(show.showTime).toLocaleTimeString("en-US", {
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
                  Seats ({seats?.length || 0})
                </p>
                <div className="flex flex-wrap gap-2">
                  {seats?.map((seat) => (
                    <span
                      key={seat.seatId}
                      className="px-4 py-2 bg-primary-100 text-primary-800 rounded-lg font-semibold"
                    >
                      {seat.rowNumber}
                      {seat.seatNumber}
                    </span>
                  )) || <span className="text-primary-600">No seats</span>}
                </div>
              </div>

              {/* Payment Summary */}
              <div className="border-t border-primary-200 pt-4">
                <div className="flex justify-between items-center">
                  <span className="text-lg font-medium text-primary-700">
                    Total Amount Paid
                  </span>
                  <span className="text-2xl font-bold text-primary-900">
                    ₹{totalAmount?.toFixed(2) || "0.00"}
                  </span>
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
