import React, { useEffect, useState } from "react";
import { getUserTickets } from "../../services/ticketService";
import { Link } from "react-router-dom";

const MyTickets = () => {
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchTickets = async () => {
      try {
        const data = await getUserTickets();
        setTickets(data);
      } catch (err) {
        setError("Failed to load tickets");
      } finally {
        setLoading(false);
      }
    };
    fetchTickets();
  }, []);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>{error}</div>;
  if (!tickets.length) return <div>No tickets found.</div>;

  return (
    <div className="space-y-4">
      <h2 className="text-2xl font-bold mb-4">My Tickets</h2>
      <ul className="space-y-2">
        {tickets.map((ticket) => (
          <li
            key={ticket.id}
            className="border rounded p-4 flex flex-col md:flex-row md:items-center justify-between"
          >
            <div>
              <div className="font-semibold">{ticket.movieName}</div>
              <div className="text-sm text-gray-500">
                {ticket.theatreName} | {ticket.screenName}
              </div>
              <div className="text-sm">
                Show: {new Date(ticket.showTime).toLocaleString()}
              </div>
              <div className="text-sm">
                Seats: {ticket.seats.map((s) => s.seatNumber).join(", ")}
              </div>
              <div className="text-sm">Status: {ticket.status}</div>
            </div>
            <Link
              to={`/ticket/${ticket.id}`}
              className="text-blue-600 hover:underline mt-2 md:mt-0"
            >
              View Ticket
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default MyTickets;
