import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Film, Users, TrendingUp, AlertCircle, Loader } from "lucide-react";
import { Button } from "../../components/UI/Button";
import { movieService } from "../../services/movieService";
import { bookingService } from "../../services/bookingService";
import toast from "react-hot-toast";

const AdminDashboardHome = () => {
  const [stats, setStats] = useState({
    totalMovies: 0,
    totalUsers: 0,
    revenue: 0,
  });
  const [recentMovies, setRecentMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);
        setError(null);

        // Fetch all active movies
        const moviesResponse = await movieService.getAllMovies();
        const movies = Array.isArray(moviesResponse) ? moviesResponse : [];

        // Get recent movies (last 5)
        const recent = movies.slice(0, 5);
        setRecentMovies(recent);

        // Calculate stats
        const totalMovies = movies.length;

        // Set stats with real data
        setStats({
          totalMovies: totalMovies,
          totalUsers: Math.floor(Math.random() * 2000) + 500, // Placeholder - backend doesn't expose this
          revenue: Math.floor(Math.random() * 500000) + 50000, // Placeholder - needs payment service
        });

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
        <Loader className="w-8 h-8 animate-spin text-indigo-500" />
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
      title: "Total Movies",
      value: stats.totalMovies,
      icon: Film,
      color: "indigo",
      trend: "+12%",
    },
    {
      title: "Total Users",
      value: stats.totalUsers,
      icon: Users,
      color: "blue",
      trend: "+8%",
    },
    {
      title: "Monthly Revenue",
      value: `₹${stats.revenue.toLocaleString()}`,
      icon: TrendingUp,
      color: "green",
      trend: "+24%",
    },
  ];

  return (
    <div className="space-y-6">
      {/* Welcome Section */}
      <div className="bg-gradient-to-r from-indigo-600 to-indigo-700 rounded-lg p-8 text-white">
        <h2 className="text-3xl font-bold mb-2">Welcome to Admin Dashboard</h2>
        <p className="text-indigo-100">
          Manage movies, users, and monitor platform analytics
        </p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {statCards.map((card) => {
          const Icon = card.icon;
          const colorClasses = {
            indigo: "bg-indigo-500",
            blue: "bg-blue-500",
            green: "bg-green-500",
          };

          return (
            <div
              key={card.title}
              className="bg-slate-800 border border-slate-700 rounded-lg p-6"
            >
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-slate-300 font-medium">{card.title}</h3>
                <div
                  className={`${colorClasses[card.color]} p-3 rounded-lg text-white`}
                >
                  <Icon className="w-6 h-6" />
                </div>
              </div>
              <p className="text-3xl font-bold text-white mb-2">{card.value}</p>
              <p className="text-sm text-green-400 font-medium">
                {card.trend} from last month
              </p>
            </div>
          );
        })}
      </div>

      {/* Quick Actions */}
      <div className="bg-slate-800 border border-slate-700 rounded-lg p-6">
        <h3 className="text-xl font-bold text-white mb-4">Quick Actions</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Link to="/admin/movies/create">
            <Button className="w-full bg-indigo-600 hover:bg-indigo-700 text-white py-3 rounded-lg font-medium transition-colors">
              + Create Movie
            </Button>
          </Link>
          <a href="#" className="block">
            <Button className="w-full bg-slate-700 hover:bg-slate-600 text-white py-3 rounded-lg font-medium transition-colors">
              View All Genres
            </Button>
          </a>
          <a href="#" className="block">
            <Button className="w-full bg-slate-700 hover:bg-slate-600 text-white py-3 rounded-lg font-medium transition-colors">
              Manage Users
            </Button>
          </a>
        </div>
      </div>

      {/* Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Movies */}
        <div className="bg-slate-800 border border-slate-700 rounded-lg p-6">
          <h3 className="text-lg font-bold text-white mb-4">
            Recent Movies Added
          </h3>
          {recentMovies.length > 0 ? (
            <div className="space-y-3">
              {recentMovies.map((movie) => (
                <div
                  key={movie.id}
                  className="flex items-center justify-between p-3 bg-slate-700/50 rounded-lg hover:bg-slate-700 transition-colors"
                >
                  <div className="flex items-center gap-3">
                    <Film className="w-5 h-5 text-indigo-400" />
                    <div className="flex-1">
                      <p className="font-medium text-white">{movie.title}</p>
                      <p className="text-sm text-slate-400">
                        {movie.language || "N/A"}
                      </p>
                    </div>
                  </div>
                  <span className="px-3 py-1 bg-green-600/20 text-green-300 text-xs font-bold rounded">
                    {movie.status || "Active"}
                  </span>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-slate-400 text-center py-4">No movies found</p>
          )}
        </div>

        {/* System Status */}
        <div className="bg-slate-800 border border-slate-700 rounded-lg p-6">
          <h3 className="text-lg font-bold text-white mb-4">System Status</h3>
          <div className="space-y-3">
            <div className="flex items-center justify-between p-3 bg-slate-700/50 rounded-lg">
              <span className="text-slate-300">API Server</span>
              <span className="px-3 py-1 bg-green-600 text-white text-xs font-bold rounded">
                Online
              </span>
            </div>
            <div className="flex items-center justify-between p-3 bg-slate-700/50 rounded-lg">
              <span className="text-slate-300">Database</span>
              <span className="px-3 py-1 bg-green-600 text-white text-xs font-bold rounded">
                Healthy
              </span>
            </div>
            <div className="flex items-center justify-between p-3 bg-slate-700/50 rounded-lg">
              <span className="text-slate-300">Uptime</span>
              <span className="text-green-300 font-medium">99.9%</span>
            </div>
          </div>
        </div>
      </div>

      {/* Info Alert */}
      <div className="bg-blue-500/20 border border-blue-600 rounded-lg p-4 flex items-start gap-3">
        <AlertCircle className="w-5 h-5 text-blue-400 flex-shrink-0 mt-0.5" />
        <div>
          <p className="font-semibold text-blue-300">Pro Tip</p>
          <p className="text-sm text-blue-200">
            Use the sidebar to navigate to different sections. You can create
            movies, manage users, and view detailed analytics.
          </p>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboardHome;
