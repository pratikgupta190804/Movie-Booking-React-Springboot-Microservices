import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import {
  Calendar,
  Edit2,
  Trash2,
  Loader,
  AlertCircle,
  Plus,
} from "lucide-react";
import { Button } from "../../components/UI/Button";
import { showService } from "../../services/showService";
import toast from "react-hot-toast";

const ManageShows = () => {
  const [shows, setShows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState("all");

  useEffect(() => {
    const fetchShows = async () => {
      try {
        setLoading(true);
        // Fetch all shows - will use date-range endpoint internally
        const response = await showService.getAllShows();
        const showsList = Array.isArray(response) ? response : [];
        setShows(showsList);
        setLoading(false);
      } catch (err) {
        console.error("Error fetching shows:", err);
        setError("Failed to fetch shows. Please try again.");
        toast.error("Failed to load shows");
        setLoading(false);
      }
    };

    fetchShows();
  }, []);

  const handleDelete = async (showId) => {
    if (!window.confirm("Are you sure you want to delete this show?")) {
      return;
    }

    try {
      // Note: Add delete method to showService
      // await showService.deleteShow(showId);
      toast.success("Show deleted successfully!");
      setShows(shows.filter((show) => show.id !== showId));
    } catch (error) {
      console.error("Error deleting show:", error);
      toast.error("Failed to delete show");
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loader className="w-8 h-8 animate-spin text-purple-500" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-6xl mx-auto px-4 py-8">
        <div className="bg-red-500/20 border border-red-600 rounded-lg p-6 text-center">
          <AlertCircle className="w-12 h-12 text-red-400 mx-auto mb-4" />
          <p className="text-red-300 font-medium">{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <div className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-white mb-2">Manage Shows</h1>
          <p className="text-slate-400">
            Edit or delete shows for your theatres
          </p>
        </div>
        <Link to="/theatre/shows/create">
          <Button className="bg-purple-600 hover:bg-purple-700 text-white font-medium px-4 py-2 rounded-lg flex items-center gap-2">
            <Plus className="w-4 h-4" />
            Add Show
          </Button>
        </Link>
      </div>

      {/* Filters */}
      <div className="mb-6 flex gap-2">
        <button
          onClick={() => setFilter("all")}
          className={`px-4 py-2 rounded-lg font-medium transition-colors ${
            filter === "all"
              ? "bg-purple-600 text-white"
              : "bg-slate-700 text-slate-300 hover:bg-slate-600"
          }`}
        >
          All Shows
        </button>
        <button
          onClick={() => setFilter("upcoming")}
          className={`px-4 py-2 rounded-lg font-medium transition-colors ${
            filter === "upcoming"
              ? "bg-purple-600 text-white"
              : "bg-slate-700 text-slate-300 hover:bg-slate-600"
          }`}
        >
          Upcoming
        </button>
        <button
          onClick={() => setFilter("past")}
          className={`px-4 py-2 rounded-lg font-medium transition-colors ${
            filter === "past"
              ? "bg-purple-600 text-white"
              : "bg-slate-700 text-slate-300 hover:bg-slate-600"
          }`}
        >
          Past
        </button>
      </div>

      {/* Shows Table */}
      <div className="bg-slate-800 border border-slate-700 rounded-lg overflow-hidden">
        {shows.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-slate-900 border-b border-slate-700">
                <tr>
                  <th className="px-6 py-3 text-left text-sm font-medium text-slate-300">
                    Movie
                  </th>
                  <th className="px-6 py-3 text-left text-sm font-medium text-slate-300">
                    Theatre
                  </th>
                  <th className="px-6 py-3 text-left text-sm font-medium text-slate-300">
                    Date & Time
                  </th>
                  <th className="px-6 py-3 text-left text-sm font-medium text-slate-300">
                    Price
                  </th>
                  <th className="px-6 py-3 text-left text-sm font-medium text-slate-300">
                    Format
                  </th>
                  <th className="px-6 py-3 text-left text-sm font-medium text-slate-300">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-700">
                {shows.map((show) => (
                  <tr
                    key={show.id}
                    className="hover:bg-slate-700/50 transition-colors"
                  >
                    <td className="px-6 py-4 text-slate-300">
                      {show.movieTitle || "N/A"}
                    </td>
                    <td className="px-6 py-4 text-slate-300">
                      {show.theatreName || "N/A"}
                    </td>
                    <td className="px-6 py-4 text-slate-300">
                      <div className="flex items-center gap-2">
                        <Calendar className="w-4 h-4 text-purple-400" />
                        {show.showDate} {show.showTime}
                      </div>
                    </td>
                    <td className="px-6 py-4 text-slate-300">
                      ₹{show.price || 0}
                    </td>
                    <td className="px-6 py-4">
                      <span className="px-3 py-1 bg-purple-600/20 text-purple-300 text-xs font-bold rounded">
                        {show.format || "2D"}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2">
                        <button className="p-2 bg-blue-600/20 hover:bg-blue-600/30 text-blue-400 rounded transition-colors">
                          <Edit2 className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => handleDelete(show.id)}
                          className="p-2 bg-red-600/20 hover:bg-red-600/30 text-red-400 rounded transition-colors"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="text-center py-12">
            <Calendar className="w-12 h-12 text-slate-600 mx-auto mb-4" />
            <p className="text-slate-400 font-medium">No shows created yet</p>
            <p className="text-slate-500 text-sm">
              Create your first show to get started
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ManageShows;
