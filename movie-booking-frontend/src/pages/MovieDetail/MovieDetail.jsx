import { useState, useEffect } from "react";
import { useParams, Link } from "react-router-dom";
import { Clock, Calendar, Globe, Star } from "lucide-react";
import { Button } from "../../components/UI/Button";
import { Loader } from "../../components/UI/Loader";
import { movieService } from "../../services/movieService";

const MovieDetail = () => {
  const { movieId } = useParams();
  const [movie, setMovie] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchMovie = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await movieService.getMovieById(movieId);
        setMovie(data);
      } catch (err) {
        console.error("Error fetching movie:", err);
        setError("Failed to load movie details. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchMovie();
  }, [movieId]);

  if (loading) {
    return <Loader fullScreen />;
  }

  if (error || !movie) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <p className="text-xl text-red-600 mb-4">
            {error || "Movie not found"}
          </p>
          <Link to="/movies">
            <Button variant="primary">Back to Movies</Button>
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-primary-50">
      {/* Hero Section */}
      <div className="bg-gradient-to-r from-primary-900 to-primary-800 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div className="grid md:grid-cols-3 gap-8">
            {/* Movie Poster */}
            <div className="md:col-span-1">
              <img
                src={
                  movie.posterUrl ||
                  "https://via.placeholder.com/400x600/1e293b/f84464?text=" +
                    encodeURIComponent(movie.title)
                }
                alt={movie.title}
                className="w-full rounded-lg shadow-2xl"
              />
            </div>

            {/* Movie Details */}
            <div className="md:col-span-2 flex flex-col justify-center">
              <h1 className="text-4xl md:text-5xl font-bold mb-4">
                {movie.title}
              </h1>

              <div className="flex flex-wrap gap-2 mb-4">
                {movie.genres?.map((g) => (
                  <span
                    key={g.id}
                    className="px-3 py-1 bg-white/20 rounded-full text-sm"
                  >
                    {g.name}
                  </span>
                ))}
              </div>

              <div className="flex items-center gap-2 mb-6">
                <Star className="h-6 w-6 text-yellow-400 fill-yellow-400" />
                <span className="text-2xl font-bold">
                  {movie.rating || "N/A"}
                </span>
                <span className="text-primary-300">/10</span>
              </div>

              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
                <div>
                  <div className="flex items-center gap-2 text-primary-300 mb-1">
                    <Clock className="h-4 w-4" />
                    <span className="text-sm">Duration</span>
                  </div>
                  <p className="font-semibold">
                    {movie.durationInMinutes} mins
                  </p>
                </div>
                <div>
                  <div className="flex items-center gap-2 text-primary-300 mb-1">
                    <Globe className="h-4 w-4" />
                    <span className="text-sm">Language</span>
                  </div>

                  <div className="flex flex-wrap gap-2">
                    {movie.languages?.map((l, index) => (
                      <span
                        key={index}
                        className="px-3 py-1 bg-white/20 rounded-full text-sm"
                      >
                        {l}
                      </span>
                    ))}
                  </div>
                </div>
                <div>
                  <div className="flex items-center gap-2 text-primary-300 mb-1">
                    <Calendar className="h-4 w-4" />
                    <span className="text-sm">Release</span>
                  </div>
                  <p className="font-semibold">
                    {movie.releaseDate
                      ? new Date(movie.releaseDate).toLocaleDateString()
                      : "TBA"}
                  </p>
                </div>
              </div>

              <Link to={`/movies/${movieId}/shows`}>
                <Button variant="primary" size="lg" className="text-lg">
                  Book Tickets
                </Button>
              </Link>
            </div>
          </div>
        </div>
      </div>

      {/* Details Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="bg-white rounded-lg shadow-md p-8">
          <h2 className="text-2xl font-bold text-primary-900 mb-4">
            About the Movie
          </h2>
          <p className="text-primary-700 leading-relaxed mb-6">
            {movie.description || "No description available."}
          </p>

          <div className="grid md:grid-cols-2 gap-6">
            <div>
              <h3 className="text-lg font-semibold text-primary-900 mb-2">
                Director
              </h3>
              <p className="text-primary-700">
                {movie.director?.name || "Unknown"}
              </p>
            </div>
            <div>
              <h3 className="text-lg font-semibold text-primary-900 mb-2">
                Cast
              </h3>
              <p className="text-primary-700">
                {movie.actors?.map((a) => a.name).join(", ") || "Unknown"}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MovieDetail;
