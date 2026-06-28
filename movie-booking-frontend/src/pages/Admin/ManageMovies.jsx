import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Film, Edit2, Trash2, Plus, Loader, AlertCircle } from "lucide-react";
import { Button } from "../../components/UI/Button";
import { movieService } from "../../services/movieService";
import toast from "react-hot-toast";

const ManageMovies = () => {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    const fetchMovies = async () => {
      try {
        setLoading(true);
        // Fetch active movies using status endpoint
        const response = await movieService.getAllMovies();
        const moviesList = Array.isArray(response) ? response : [];
        setMovies(moviesList);
        setLoading(false);
      } catch (err) {
        console.error("Error fetching movies:", err);
        setError("Failed to fetch movies");
        toast.error("Failed to load movies");
        setLoading(false);
      }
    };

    fetchMovies();
  }, []);

  const handleDeleteMovie = async (movieId) => {
    if (!window.confirm("Are you sure you want to delete this movie?")) {
      return;
    }

    try {
      await movieService.deleteMovie(movieId);
      toast.success("Movie deleted successfully!");
      setMovies(movies.filter((m) => m.id !== movieId));
    } catch (error) {
      console.error("Error deleting movie:", error);
      toast.error("Failed to delete movie");
    }
  };

  const handleToggleStatus = async (movieId, currentStatus) => {
    try {
      const newStatus = currentStatus === "ACTIVE" ? "INACTIVE" : "ACTIVE";
      await movieService.updateMovieStatus(movieId, newStatus);
      toast.success(`Movie status updated to ${newStatus.toLowerCase()}`);
      setMovies(
        movies.map((m) => (m.id === movieId ? { ...m, status: newStatus } : m)),
      );
    } catch (error) {
      console.error("Error updating movie status:", error);
      toast.error("Failed to update movie status");
    }
  };

  const filteredMovies = movies.filter(
    (movie) =>
      movie.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      movie.slug?.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loader className="w-8 h-8 animate-spin text-indigo-500" />
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
          <h1 className="text-3xl font-bold text-white mb-2">Manage Movies</h1>
          <p className="text-slate-400">View, edit, or delete movies</p>
        </div>
        <Link to="/admin/movies/create">
          <Button className="bg-indigo-600 hover:bg-indigo-700 text-white font-medium px-4 py-2 rounded-lg flex items-center gap-2">
            <Plus className="w-4 h-4" />
            Add Movie
          </Button>
        </Link>
      </div>

      {/* Search Bar */}
      <div className="mb-6">
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Search movies by title or slug..."
          className="w-full px-4 py-2 bg-slate-800 border border-slate-700 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:border-indigo-500"
        />
      </div>

      {/* Movies Table */}
      <div className="bg-slate-800 border border-slate-700 rounded-lg overflow-hidden">
        {filteredMovies.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-slate-900 border-b border-slate-700">
                <tr>
                  <th className="px-6 py-3 text-left text-sm font-medium text-slate-300">
                    Title
                  </th>
                  <th className="px-6 py-3 text-left text-sm font-medium text-slate-300">
                    Language
                  </th>
                  <th className="px-6 py-3 text-left text-sm font-medium text-slate-300">
                    Rating
                  </th>
                  <th className="px-6 py-3 text-left text-sm font-medium text-slate-300">
                    Duration
                  </th>
                  <th className="px-6 py-3 text-left text-sm font-medium text-slate-300">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-sm font-medium text-slate-300">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-700">
                {filteredMovies.map((movie) => (
                  <tr
                    key={movie.id}
                    className="hover:bg-slate-700/50 transition-colors"
                  >
                    <td className="px-6 py-4 text-slate-300">{movie.title}</td>
                    <td className="px-6 py-4 text-slate-300">
                      {movie.language || "N/A"}
                    </td>
                    <td className="px-6 py-4 text-slate-300">
                      {movie.rating || "N/A"}
                    </td>
                    <td className="px-6 py-4 text-slate-300">
                      {movie.duration ? `${movie.duration} min` : "N/A"}
                    </td>
                    <td className="px-6 py-4">
                      <button
                        onClick={() =>
                          handleToggleStatus(movie.id, movie.status)
                        }
                        className={`px-3 py-1 text-xs font-bold rounded cursor-pointer transition-colors ${
                          movie.status === "ACTIVE"
                            ? "bg-green-600/20 text-green-300 hover:bg-green-600/30"
                            : "bg-red-600/20 text-red-300 hover:bg-red-600/30"
                        }`}
                      >
                        {movie.status || "ACTIVE"}
                      </button>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2">
                        <Link to={`/admin/movies/${movie.id}/edit`}>
                          <button className="p-2 bg-blue-600/20 hover:bg-blue-600/30 text-blue-400 rounded transition-colors">
                            <Edit2 className="w-4 h-4" />
                          </button>
                        </Link>
                        <button
                          onClick={() => handleDeleteMovie(movie.id)}
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
            <Film className="w-12 h-12 text-slate-600 mx-auto mb-4" />
            <p className="text-slate-400 font-medium">
              {searchTerm ? "No movies found" : "No movies created yet"}
            </p>
            <p className="text-slate-500 text-sm">
              {searchTerm
                ? "Try a different search term"
                : "Create your first movie to get started"}
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ManageMovies;
