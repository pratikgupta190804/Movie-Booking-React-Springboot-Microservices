import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Calendar, MapPin, Users, AlertCircle, Loader } from "lucide-react";
import { Button } from "../../components/UI/Button";
import { showService } from "../../services/showService";
import { theatreService } from "../../services/theatreService";
import { movieService } from "../../services/movieService";
import toast from "react-hot-toast";

const CreateShow = () => {
  const navigate = useNavigate();
  const [theatres, setTheatres] = useState([]);
  const [movies, setMovies] = useState([]);
  const [screens, setScreens] = useState([]);
  const [loading, setLoading] = useState(false);
  const [fetchingData, setFetchingData] = useState(true);
  const [loadingScreens, setLoadingScreens] = useState(false);

  const [formData, setFormData] = useState({
    movieId: "",
    theatreId: "",
    screenId: "",
    showDate: "",
    showTime: "",
    price: "",
    language: "",
    format: "2D",
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        setFetchingData(true);
        // Fetch theatres and movies for dropdowns
        const [theatresRes, moviesRes] = await Promise.all([
          theatreService.getAllTheatres().catch(() => []),
          movieService.getAllMovies().catch(() => []),
        ]);
        setTheatres(Array.isArray(theatresRes) ? theatresRes : []);
        setMovies(Array.isArray(moviesRes) ? moviesRes : []);
        setFetchingData(false);
      } catch (error) {
        console.error("Error fetching data:", error);
        toast.error("Failed to load theatre data");
        setFetchingData(false);
      }
    };
    fetchData();
  }, []);

  // Fetch screens when theatre changes
  useEffect(() => {
    const fetchScreens = async () => {
      if (!formData.theatreId) {
        setScreens([]);
        setFormData((prev) => ({ ...prev, screenId: "" }));
        return;
      }
      try {
        setLoadingScreens(true);
        const screensRes = await theatreService.getScreensByTheatre(formData.theatreId);
        setScreens(Array.isArray(screensRes) ? screensRes : []);
      } catch (error) {
        console.error("Error fetching screens:", error);
        toast.error("Failed to load screens for the selected theatre");
      } finally {
        setLoadingScreens(false);
      }
    };
    fetchScreens();
  }, [formData.theatreId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (
      !formData.movieId ||
      !formData.theatreId ||
      !formData.screenId ||
      !formData.showDate ||
      !formData.showTime
    ) {
      toast.error("Please fill all required fields");
      return;
    }

    setLoading(true);
    try {
      // Calculate start and end times
      const startTime = `${formData.showDate}T${formData.showTime}:00`;
      const selectedMovie = movies.find((m) => String(m.id) === String(formData.movieId));
      const durationMin = selectedMovie ? parseInt(selectedMovie.duration) : 120;
      
      const start = new Date(startTime);
      const end = new Date(start.getTime() + durationMin * 60 * 1000);
      
      const pad = (num) => String(num).padStart(2, "0");
      const endTime = `${end.getFullYear()}-${pad(end.getMonth() + 1)}-${pad(
        end.getDate()
      )}T${pad(end.getHours())}:${pad(end.getMinutes())}:${pad(end.getSeconds())}`;

      const basePrice = parseFloat(formData.price) || 200;

      const showPayload = {
        movieId: String(formData.movieId),
        theatreId: String(formData.theatreId),
        screenId: String(formData.screenId),
        language: formData.language || (selectedMovie?.language || "English"),
        startTime,
        endTime,
        price: basePrice,
        seatPrices: [
          { rowLabel: "A", seatType: "REGULAR", price: basePrice },
          { rowLabel: "B", seatType: "REGULAR", price: basePrice },
          { rowLabel: "C", seatType: "REGULAR", price: basePrice },
          { rowLabel: "D", seatType: "PREMIUM", price: basePrice + 50 },
          { rowLabel: "E", seatType: "RECLINER", price: basePrice + 150 },
        ],
      };

      await showService.createShow(showPayload);
      toast.success("Show created successfully!");
      navigate("/theatre/shows");
    } catch (error) {
      console.error("Error creating show:", error);
      const errorMsg = error.response?.data?.message || "Failed to create show";
      toast.error(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  if (fetchingData) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loader className="w-8 h-8 animate-spin text-purple-500" />
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto px-4 py-8">
      <div className="bg-slate-800 border border-slate-700 rounded-lg p-8">
        <h1 className="text-3xl font-bold text-white mb-2">Create New Show</h1>
        <p className="text-slate-400 mb-8">
          Add a new movie show to your theatre
        </p>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Theatre Selection */}
          <div>
            <label className="block text-sm font-medium text-white mb-2">
              Theatre *
            </label>
            <select
              name="theatreId"
              value={formData.theatreId}
              onChange={handleChange}
              className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:outline-none focus:border-purple-500"
            >
              <option value="">Select Theatre</option>
              {theatres.map((theatre) => (
                <option key={theatre.id} value={theatre.id}>
                  {theatre.name} - {theatre.city}
                </option>
              ))}
            </select>
          </div>

          {/* Screen Selection */}
          <div>
            <label className="block text-sm font-medium text-white mb-2">
              Screen *
            </label>
            <select
              name="screenId"
              value={formData.screenId}
              onChange={handleChange}
              disabled={!formData.theatreId || loadingScreens}
              className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:outline-none focus:border-purple-500 disabled:opacity-50"
            >
              <option value="">
                {loadingScreens
                  ? "Loading screens..."
                  : !formData.theatreId
                  ? "Select a theatre first"
                  : "Select Screen"}
              </option>
              {screens.map((screen) => (
                <option key={screen.id} value={screen.id}>
                  {screen.name} ({screen.seatingCapacity} seats)
                </option>
              ))}
            </select>
          </div>

          {/* Movie Selection */}
          <div>
            <label className="block text-sm font-medium text-white mb-2">
              Movie *
            </label>
            <select
              name="movieId"
              value={formData.movieId}
              onChange={handleChange}
              className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:outline-none focus:border-purple-500"
            >
              <option value="">Select Movie</option>
              {movies.map((movie) => (
                <option key={movie.id} value={movie.id}>
                  {movie.title} ({movie.language || "N/A"})
                </option>
              ))}
            </select>
          </div>

          {/* Show Date & Time */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-white mb-2">
                Show Date *
              </label>
              <input
                type="date"
                name="showDate"
                value={formData.showDate}
                onChange={handleChange}
                className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:outline-none focus:border-purple-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-white mb-2">
                Show Time *
              </label>
              <input
                type="time"
                name="showTime"
                value={formData.showTime}
                onChange={handleChange}
                className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:outline-none focus:border-purple-500"
              />
            </div>
          </div>

          {/* Price & Language */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-white mb-2">
                Price (₹)
              </label>
              <input
                type="number"
                name="price"
                value={formData.price}
                onChange={handleChange}
                placeholder="300"
                className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:border-purple-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-white mb-2">
                Language
              </label>
              <select
                name="language"
                value={formData.language}
                onChange={handleChange}
                className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:outline-none focus:border-purple-500"
              >
                <option value="">Select Language</option>
                <option value="English">English</option>
                <option value="Hindi">Hindi</option>
                <option value="Tamil">Tamil</option>
                <option value="Telugu">Telugu</option>
                <option value="Kannada">Kannada</option>
                <option value="Malayalam">Malayalam</option>
              </select>
            </div>
          </div>

          {/* Format */}
          <div>
            <label className="block text-sm font-medium text-white mb-2">
              Format
            </label>
            <select
              name="format"
              value={formData.format}
              onChange={handleChange}
              className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:outline-none focus:border-purple-500"
            >
              <option value="2D">2D</option>
              <option value="3D">3D</option>
              <option value="IMAX">IMAX</option>
              <option value="4DX">4DX</option>
            </select>
          </div>

          {/* Info Alert */}
          <div className="bg-blue-500/20 border border-blue-600 rounded-lg p-4 flex items-start gap-3">
            <AlertCircle className="w-5 h-5 text-blue-400 flex-shrink-0 mt-0.5" />
            <p className="text-sm text-blue-200">
              Make sure to select a valid theatre and screen before creating the
              show.
            </p>
          </div>

          {/* Actions */}
          <div className="flex gap-4">
            <Button
              type="submit"
              disabled={loading}
              className="flex-1 bg-purple-600 hover:bg-purple-700 text-white font-medium py-3 rounded-lg transition-colors disabled:opacity-50"
            >
              {loading ? (
                <>
                  <Loader className="w-4 h-4 animate-spin inline mr-2" />
                  Creating...
                </>
              ) : (
                "Create Show"
              )}
            </Button>
            <Button
              type="button"
              onClick={() => navigate(-1)}
              className="flex-1 bg-slate-700 hover:bg-slate-600 text-white font-medium py-3 rounded-lg transition-colors"
            >
              Cancel
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateShow;
