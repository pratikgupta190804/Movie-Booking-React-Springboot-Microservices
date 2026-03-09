import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Search, Filter } from "lucide-react";
import { Card } from "../../components/UI/Card";
import { Input } from "../../components/UI/Input";
import { Loader } from "../../components/UI/Loader";
import { Button } from "../../components/UI/Button";
import { movieService } from "../../services/movieService";

const Movies = () => {
  const [movies, setMovies] = useState([]);
  const [filteredMovies, setFilteredMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedGenre, setSelectedGenre] = useState("all");

  // Fetch movies from API
  useEffect(() => {
    const fetchMovies = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await movieService.getMoviesByStatus("NOW_SHOWING");
        setMovies(data);
        setFilteredMovies(data);
      } catch (err) {
        console.error("Error fetching movies:", err);
        setError("Failed to load movies. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchMovies();
  }, []);

  useEffect(() => {
    let filtered = movies;

    // Filter by search query
    if (searchQuery) {
      filtered = filtered.filter((movie) =>
        movie.title.toLowerCase().includes(searchQuery.toLowerCase()),
      );
    }

    // Filter by genre
    if (selectedGenre !== "all") {
      filtered = filtered.filter((movie) =>
        movie.genres?.some((g) => g.name === selectedGenre),
      );
    }

    setFilteredMovies(filtered);
  }, [searchQuery, selectedGenre, movies]);

  // Extract unique genres from movies
  const genres = [
    "all",
    ...new Set(
      movies.flatMap((movie) => movie.genres?.map((g) => g.name) || []),
    ),
  ];

  return (
    <div className="min-h-screen bg-primary-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-4xl font-bold text-primary-900 mb-8">All Movies</h1>

        {/* Filters */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-8">
          <div className="grid md:grid-cols-2 gap-4">
            <Input
              placeholder="Search movies..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              icon={Search}
            />
            <div>
              <label className="block text-sm font-medium text-primary-700 mb-2">
                <Filter className="inline h-4 w-4 mr-1" />
                Filter by Genre
              </label>
              <select
                value={selectedGenre}
                onChange={(e) => setSelectedGenre(e.target.value)}
                className="w-full px-4 py-2 border border-primary-300 rounded-lg focus:ring-2 focus:ring-accent focus:border-transparent"
              >
                {genres.map((genre) => (
                  <option key={genre} value={genre}>
                    {genre === "all" ? "All Genres" : genre}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>

        {/* Movies Grid */}
        {loading ? (
          <Loader />
        ) : error ? (
          <div className="text-center py-12">
            <p className="text-xl text-red-600">{error}</p>
          </div>
        ) : filteredMovies.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-xl text-primary-600">No movies found</p>
          </div>
        ) : (
          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {filteredMovies.map((movie) => (
              <Link key={movie.id} to={`/movies/${movie.id}`}>
                <Card hover className="group" >
                  <div className="relative overflow-hidden">
                    <img
                      src={
                        movie.posterUrl ||
                        "https://via.placeholder.com/300x450/1e293b/f84464?text=" +
                          encodeURIComponent(movie.title)
                      }
                      alt={movie.title}
                      className="w-full h-80 object-cover group-hover:scale-110 transition-transform duration-300"
                    />
                    <div className="absolute top-2 right-2 bg-black/70 text-white px-3 py-1 rounded-full text-sm font-semibold">
                      ⭐ {movie.rating || "N/A"}
                    </div>
                  </div>
                  <div className="p-4">
                    <h3 className="text-lg font-semibold text-primary-900 mb-2 truncate">
                      {movie.title}
                    </h3>
                    <div className="space-y-1 text-sm text-primary-600 mb-3">
                      <p>
                        {movie.genres?.map((g) => g.name).join(", ") ||
                          "Unknown"}
                      </p>
                      <p>
                        {movie.durationInMinutes} mins • {movie.language}
                      </p>
                    </div>
                    <Button variant="primary" size="sm" className="w-full">
                      Book Now
                    </Button>
                  </div>
                </Card>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Movies;
