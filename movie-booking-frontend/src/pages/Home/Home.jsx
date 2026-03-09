import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Film, Calendar, MapPin, TrendingUp } from "lucide-react";
import { Button } from "../../components/UI/Button";
import { Card } from "../../components/UI/Card";
import { Loader } from "../../components/UI/Loader";
import { movieService } from "../../services/movieService";

const Home = () => {
  const [loading, setLoading] = useState(true);
  const [featuredMovies, setFeaturedMovies] = useState([]);

  useEffect(() => {
    const fetchFeaturedMovies = async () => {
      try {
        setLoading(true);
        const data = await movieService.getMoviesByStatus("NOW_SHOWING");
        // Get first 4 movies for featured section
        setFeaturedMovies(data.slice(0, 4));
      } catch (error) {
        console.error("Error fetching featured movies:", error);
        setFeaturedMovies([]);
      } finally {
        setLoading(false);
      }
    };

    fetchFeaturedMovies();
  }, []);

  return (
    <div>
      {/* Hero Section */}
      <section className="bg-gradient-to-r from-primary-900 to-primary-800 text-white py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-3xl mx-auto">
            <h1 className="text-5xl md:text-6xl font-bold mb-6">
              Book Your Movie Tickets Online
            </h1>
            <p className="text-xl text-primary-200 mb-8">
              Experience the best of cinema with easy booking, multiple
              theatres, and secure payments
            </p>
            <Link to="/movies">
              <Button variant="primary" size="lg" className="text-lg">
                Book Tickets Now
              </Button>
            </Link>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <h2 className="text-3xl font-bold text-center text-primary-900 mb-12">
            Why Choose Us
          </h2>
          <div className="grid md:grid-cols-3 gap-8">
            <Card className="text-center p-8">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-accent/10 rounded-full mb-4">
                <Film className="h-8 w-8 text-accent" />
              </div>
              <h3 className="text-xl font-semibold text-primary-900 mb-2">
                Latest Movies
              </h3>
              <p className="text-primary-600">
                Watch the latest blockbusters and releases at your nearest
                theatre
              </p>
            </Card>

            <Card className="text-center p-8">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-accent/10 rounded-full mb-4">
                <MapPin className="h-8 w-8 text-accent" />
              </div>
              <h3 className="text-xl font-semibold text-primary-900 mb-2">
                Multiple Theatres
              </h3>
              <p className="text-primary-600">
                Choose from a wide range of theatres across your city
              </p>
            </Card>

            <Card className="text-center p-8">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-accent/10 rounded-full mb-4">
                <Calendar className="h-8 w-8 text-accent" />
              </div>
              <h3 className="text-xl font-semibold text-primary-900 mb-2">
                Easy Booking
              </h3>
              <p className="text-primary-600">
                Book your seats in just a few clicks with our simple interface
              </p>
            </Card>
          </div>
        </div>
      </section>

      {/* Featured Movies */}
      <section className="py-16 bg-primary-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between mb-8">
            <h2 className="text-3xl font-bold text-primary-900 flex items-center gap-2">
              <TrendingUp className="h-8 w-8 text-accent" />
              Now Showing
            </h2>
            <Link to="/movies">
              <Button variant="outline">View All Movies</Button>
            </Link>
          </div>

          {loading ? (
            <Loader />
          ) : featuredMovies.length === 0 ? (
            <div className="text-center py-12">
              <p className="text-lg text-primary-600">
                No movies available at the moment
              </p>
            </div>
          ) : (
            <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
              {featuredMovies.map((movie) => (
                <Link key={movie.movieId} to={`/movies/${movie.movieId}`}>
                  <Card hover className="group">
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
                      <div className="absolute top-2 right-2 bg-black/70 text-white px-3 py-1 rounded-full text-sm font-semibold flex items-center gap-1">
                        ⭐ {movie.rating || "N/A"}
                      </div>
                    </div>
                    <div className="p-4">
                      <h3 className="text-lg font-semibold text-primary-900 mb-1 truncate">
                        {movie.title}
                      </h3>
                      <p className="text-sm text-primary-600">
                        {movie.genres?.map((g) => g.name).join(", ") ||
                          "Unknown"}
                      </p>
                      <Button
                        variant="primary"
                        size="sm"
                        className="w-full mt-4"
                      >
                        Book Now
                      </Button>
                    </div>
                  </Card>
                </Link>
              ))}
            </div>
          )}
        </div>
      </section>
    </div>
  );
};

export default Home;
