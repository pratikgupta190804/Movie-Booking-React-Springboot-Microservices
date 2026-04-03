import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import {
  Building2,
  Ticket,
  TrendingUp,
  AlertCircle,
  Loader,
  Calendar,
} from "lucide-react";
import { Button } from "../../components/UI/Button";
import { useAuth } from "../../context/AuthContext";
import { theatreService } from "../../services/theatreService";
import { showService } from "../../services/showService";
import { bookingService } from "../../services/bookingService";
import toast from "react-hot-toast";

const TheatreOwnerDashboardHome = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    totalTheatres: 0,
    activeShows: 0,
    totalBookings: 0,
    revenue: 0,
  });
  const [theatres, setTheatres] = useState([]);
  const [recentBookings, setRecentBookings] = useState([]);
  const [topShows, setTopShows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);
        setError(null);

        // Fetch all theatres (using search with empty keyword)
        const theatresResponse = await theatreService.getAllTheatres();
        const allTheatres = Array.isArray(theatresResponse)
          ? theatresResponse
          : [];

        // Show limited theatres on dashboard (first 3)
        setTheatres(allTheatres.slice(0, 3));

        // Fetch shows
        const showsResponse = await showService.getAllShows();
        const allShows = Array.isArray(showsResponse) ? showsResponse : [];

        // Get top shows (most bookings simulation)
        const topShowsList = allShows.slice(0, 3).map((show) => ({
          id: show.id,
          title: show.movieTitle || show.movieName || "Untitled",
          bookings: Math.floor(Math.random() * 100) + 50,
        }));
        setTopShows(topShowsList);

        // Calculate stats
        const totalTheatres = allTheatres.length;
        const activeShows = allShows.length;

        // Mock revenue calculation
        let totalBookingsCount = 0;
        let totalRevenue = 0;

        topShowsList.forEach((show) => {
          totalBookingsCount += show.bookings;
          totalRevenue += show.bookings * 500; // Assuming ₹500 per booking average
        });

        setStats({
          totalTheatres: totalTheatres,
          activeShows: activeShows,
          totalBookings: totalBookingsCount,
          revenue: totalRevenue,
        });

        // Simulate recent bookings
        const mockBookings = Array.from({ length: 5 }, (_, i) => ({
          id: `BK${10001 + i}`,
          movieTitle: allShows[i]?.movieTitle || `Movie ${i + 1}`,
          tickets: 2 + (i % 3),
          amount: 500 * (2 + (i % 3)),
          status: "CONFIRMED",
        }));
        setRecentBookings(mockBookings);

        setLoading(false);
      } catch (err) {
        console.error("Error fetching dashboard data:", err);
        setError("Failed to fetch dashboard data. Please try again.");
        toast.error("Failed to load dashboard data");
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loader className="w-8 h-8 animate-spin text-purple-500" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="bg-red-500/20 border border-red-600 rounded-lg p-6 text-center">
          <AlertCircle className="w-12 h-12 text-red-400 mx-auto mb-4" />
          <p className="text-red-300 font-medium">{error}</p>
          <Button
            className="mt-4 bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded"
            onClick={() => window.location.reload()}
          >
            Retry
          </Button>
        </div>
      </div>
    );
  }

  const statCards = [
    {
      title: "My Theatres",
      value: stats.totalTheatres,
      icon: Building2,
      color: "purple",
      trend: "+1 this month",
    },
    {
      title: "Active Shows",
      value: stats.activeShows,
      icon: Calendar,
      color: "blue",
      trend: "+4 this week",
    },
    {
      title: "Total Bookings",
      value: stats.totalBookings,
      icon: Ticket,
      color: "orange",
      trend: "+60 today",
    },
    {
      title: "Revenue",
      value: `₹${stats.revenue.toLocaleString()}`,
      icon: TrendingUp,
      color: "green",
      trend: "+35% from last month",
    },
  ];

  return (
    <div className="space-y-6">
      {/* Welcome Section */}
      <div className="bg-gradient-to-r from-purple-600 to-purple-700 rounded-lg p-8 text-white">
        <h2 className="text-3xl font-bold mb-2">
          Welcome to Theatre Owner Dashboard
        </h2>
        <p className="text-purple-100">
          Manage your theatres, shows, and track revenue and bookings
        </p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {statCards.map((card) => {
          const Icon = card.icon;
          const colorClasses = {
            purple: "bg-purple-500",
            blue: "bg-blue-500",
            orange: "bg-orange-500",
            green: "bg-green-500",
          };

          return (
            <div
              key={card.title}
              className="bg-slate-800 border border-slate-700 rounded-lg p-6"
            >
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-slate-300 font-medium text-sm">
                  {card.title}
                </h3>
                <div
                  className={`${colorClasses[card.color]} p-3 rounded-lg text-white`}
                >
                  <Icon className="w-5 h-5" />
                </div>
              </div>
              <p className="text-3xl font-bold text-white mb-2">{card.value}</p>
              <p className="text-xs text-green-400 font-medium">{card.trend}</p>
            </div>
          );
        })}
      </div>

      {/* Quick Actions */}
      <div className="bg-slate-800 border border-slate-700 rounded-lg p-6">
        <h3 className="text-xl font-bold text-white mb-4">Quick Actions</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Link to="/theatre/create">
            <Button className="w-full bg-purple-600 hover:bg-purple-700 text-white py-3 rounded-lg font-medium transition-colors">
              + Create Theatre
            </Button>
          </Link>
          <Link to="/theatre/shows/create">
            <Button className="w-full bg-slate-700 hover:bg-slate-600 text-white py-3 rounded-lg font-medium transition-colors">
              + Add Show
            </Button>
          </Link>
          <a href="#" className="block">
            <Button className="w-full bg-slate-700 hover:bg-slate-600 text-white py-3 rounded-lg font-medium transition-colors">
              View Revenue
            </Button>
          </a>
        </div>
      </div>

      {/* Content Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* My Theatres */}
        <div className="lg:col-span-2 bg-slate-800 border border-slate-700 rounded-lg p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-bold text-white">My Theatres</h3>
            <Link
              to="/theatre/my-theatres"
              className="text-purple-400 hover:text-purple-300 text-sm font-medium"
            >
              View All →
            </Link>
          </div>
          {theatres.length > 0 ? (
            <div className="space-y-3">
              {theatres.map((theatre) => (
                <div
                  key={theatre.id}
                  className="flex items-center justify-between p-4 bg-slate-700/50 rounded-lg hover:bg-slate-700 transition-colors"
                >
                  <div className="flex items-center gap-4">
                    <Building2 className="w-5 h-5 text-purple-400" />
                    <div>
                      <p className="font-medium text-white">{theatre.name}</p>
                      <p className="text-sm text-slate-400">
                        Location: {theatre.city || "N/A"}
                      </p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-sm text-slate-300">
                      {theatre.screens ? theatre.screens.length : 0} Screens
                    </p>
                    <p className="text-xs text-slate-400">
                      {theatre.isActive ? "Active" : "Inactive"}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-slate-400 text-center py-4">
              No theatres found. Create one to get started!
            </p>
          )}
        </div>

        {/* Upcoming Shows */}
        <div className="bg-slate-800 border border-slate-700 rounded-lg p-6">
          <h3 className="text-lg font-bold text-white mb-4">
            Top Shows This Week
          </h3>
          {topShows.length > 0 ? (
            <div className="space-y-3">
              {topShows.map((show, idx) => (
                <div key={show.id} className="p-3 bg-slate-700/50 rounded-lg">
                  <p className="text-sm font-medium text-white">{show.title}</p>
                  <p className="text-xs text-slate-400 mt-1">
                    {show.bookings} bookings today
                  </p>
                  <div className="mt-2 w-full bg-slate-600 rounded-full h-2">
                    <div
                      className="bg-gradient-to-r from-purple-500 to-purple-400 h-2 rounded-full"
                      style={{
                        width: `${Math.min(100, (show.bookings / 100) * 100)}%`,
                      }}
                    />
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-slate-400 text-center py-4">
              No shows available
            </p>
          )}
        </div>
      </div>

      {/* Recent Bookings */}
      <div className="bg-slate-800 border border-slate-700 rounded-lg p-6">
        <h3 className="text-lg font-bold text-white mb-4">Recent Bookings</h3>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-slate-700">
                <th className="px-4 py-2 text-left text-slate-300 font-medium">
                  Booking ID
                </th>
                <th className="px-4 py-2 text-left text-slate-300 font-medium">
                  Movie
                </th>
                <th className="px-4 py-2 text-left text-slate-300 font-medium">
                  Tickets
                </th>
                <th className="px-4 py-2 text-left text-slate-300 font-medium">
                  Amount
                </th>
                <th className="px-4 py-2 text-left text-slate-300 font-medium">
                  Status
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-700">
              {recentBookings.length > 0 ? (
                recentBookings.map((booking) => (
                  <tr
                    key={booking.id}
                    className="hover:bg-slate-700/50 transition-colors"
                  >
                    <td className="px-4 py-2 text-slate-300">{booking.id}</td>
                    <td className="px-4 py-2 text-slate-300">
                      {booking.movieTitle}
                    </td>
                    <td className="px-4 py-2 text-slate-300">
                      {booking.tickets} tickets
                    </td>
                    <td className="px-4 py-2 text-slate-300">
                      ₹{booking.amount.toLocaleString()}
                    </td>
                    <td className="px-4 py-2">
                      <span className="px-2 py-1 bg-green-600/20 text-green-300 text-xs font-bold rounded">
                        {booking.status}
                      </span>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td
                    colSpan="5"
                    className="px-4 py-4 text-center text-slate-400"
                  >
                    No bookings yet
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Info Alert */}
      <div className="bg-blue-500/20 border border-blue-600 rounded-lg p-4 flex items-start gap-3">
        <AlertCircle className="w-5 h-5 text-blue-400 flex-shrink-0 mt-0.5" />
        <div>
          <p className="font-semibold text-blue-300">Pro Tip</p>
          <p className="text-sm text-blue-200">
            Track your earnings in real-time, manage shows across multiple
            theatres, and provide your customers with the best cinema
            experience.
          </p>
        </div>
      </div>
    </div>
  );
};

export default TheatreOwnerDashboardHome;
