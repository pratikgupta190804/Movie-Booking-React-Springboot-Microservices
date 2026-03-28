import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getTicketByBookingId } from "../../services/ticketService";

const TicketByBooking = () => {
  const { bookingId } = useParams();
  const [ticket, setTicket] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchTicket = async () => {
      try {
        const data = await getTicketByBookingId(bookingId);
        setTicket(data);
      } catch (err) {
        setError("Failed to load ticket");
      } finally {
        setLoading(false);
      }
    };
    fetchTicket();
  }, [bookingId]);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>{error}</div>;
  if (!ticket) return <div>Ticket not found.</div>;

  return (
    <div className="max-w-lg mx-auto border rounded p-6 mt-6 bg-white shadow">
      <h2 className="text-2xl font-bold mb-2">{ticket.movieName}</h2>
      <img
        src={ticket.moviePosterUrl}
        alt="Movie Poster"
        className="w-32 h-48 object-cover mb-2"
      />
      <div className="mb-2">Theatre: {ticket.theatreName}</div>
      <div className="mb-2">Screen: {ticket.screenName}</div>
      <div className="mb-2">
        Show Time: {new Date(ticket.showTime).toLocaleString()}
      </div>
      <div className="mb-2">
        Language: {ticket.language} | Format: {ticket.format}
      </div>
      <div className="mb-2">Booking Reference: {ticket.bookingReference}</div>
      <div className="mb-2">
        Seats: {ticket.seats.map((s) => s.seatNumber).join(", ")}
      </div>
      <div className="mb-2">Total Amount: ₹{ticket.totalAmount}</div>
      <div className="mb-2">Status: {ticket.status}</div>
      {ticket.qrCode && (
        <div className="mt-4">
          <img
            src={`data:image/png;base64,${ticket.qrCode}`}
            alt="Ticket QR Code"
            className="w-32 h-32 mx-auto"
          />
        </div>
      )}
      <div className="text-xs text-gray-500 mt-2">
        Generated at: {new Date(ticket.generatedAt).toLocaleString()}
      </div>
    </div>
  );
};

export default TicketByBooking;
