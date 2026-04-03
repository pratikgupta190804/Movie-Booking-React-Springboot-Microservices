import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Film, AlertCircle, Loader } from "lucide-react";
import { useAuth } from "../../context/AuthContext";
import { movieService } from "../../services/movieService";
import { Button } from "../../components/UI/Button";
import { Input } from "../../components/UI/Input";
import toast from "react-hot-toast";

const CreateMovie = () => {
  const navigate = useNavigate();
  const { hasRole, loading: authLoading } = useAuth();
  const [formData, setFormData] = useState({
    title: "",
    slug: "",
    description: "",
    posterUrl: "",
    duration: "",
    releaseDate: "",
    language: "",
    rating: "",
    genreIds: [],
    actorIds: [],
  });

  const [genres, setGenres] = useState([]);
  const [actors, setActors] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Redirect if not admin
  useEffect(() => {
    if (!authLoading && !hasRole("ADMIN")) {
      navigate("/");
      toast.error("You do not have permission to create movies");
    }
  }, [authLoading, hasRole, navigate]);

  // Load genres and actors
  useEffect(() => {
    const loadData = async () => {
      try {
        const [genreList, actorList] = await Promise.all([
          movieService.getAllGenres(),
          movieService.getAllActors(),
        ]);
        setGenres(genreList || []);
        setActors(actorList || []);
      } catch (err) {
        console.error("Error loading genres and actors:", err);
      }
    };

    if (!authLoading) {
      loadData();
    }
  }, [authLoading]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleGenreChange = (genreId) => {
    setFormData((prev) => ({
      ...prev,
      genreIds: prev.genreIds.includes(genreId)
        ? prev.genreIds.filter((id) => id !== genreId)
        : [...prev.genreIds, genreId],
    }));
  };

  const handleActorChange = (actorId) => {
    setFormData((prev) => ({
      ...prev,
      actorIds: prev.actorIds.includes(actorId)
        ? prev.actorIds.filter((id) => id !== actorId)
        : [...prev.actorIds, actorId],
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      // Validate required fields
      if (
        !formData.title ||
        !formData.slug ||
        !formData.duration ||
        !formData.releaseDate
      ) {
        setError("Please fill in all required fields");
        setLoading(false);
        return;
      }

      const moviePayload = {
        title: formData.title,
        slug: formData.slug,
        description: formData.description,
        posterUrl: formData.posterUrl,
        duration: parseInt(formData.duration),
        releaseDate: formData.releaseDate,
        language: formData.language,
        rating: formData.rating ? parseFloat(formData.rating) : null,
        genreIds: formData.genreIds,
        actorIds: formData.actorIds,
      };

      const response = await movieService.createMovie(moviePayload);
      toast.success("Movie created successfully!");
      navigate(`/movies/${response.id}`);
    } catch (err) {
      const errorMessage =
        err.response?.data?.message || err.message || "Failed to create movie";
      setError(errorMessage);
      toast.error(errorMessage);
      console.error("Error creating movie:", err);
    } finally {
      setLoading(false);
    }
  };

  if (authLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Loader className="w-8 h-8 animate-spin" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 to-slate-800 pt-20 pb-10">
      <div className="max-w-4xl mx-auto px-4">
        {/* Header */}
        <div className="mb-8">
          <div className="flex items-center gap-3 mb-4">
            <Film className="w-8 h-8 text-indigo-500" />
            <h1 className="text-4xl font-bold text-white">Create New Movie</h1>
          </div>
          <p className="text-slate-400">
            Add a new movie to the catalog. Only admins can create movies.
          </p>
        </div>

        {/* Error Alert */}
        {error && (
          <div className="mb-6 p-4 bg-red-500/20 border border-red-500 rounded-lg flex items-start gap-3">
            <AlertCircle className="w-5 h-5 text-red-500 mt-0.5 flex-shrink-0" />
            <p className="text-red-200">{error}</p>
          </div>
        )}

        {/* Form */}
        <form
          onSubmit={handleSubmit}
          className="bg-slate-800 border border-slate-700 rounded-lg p-8 space-y-6"
        >
          {/* Title */}
          <div>
            <label className="block text-sm font-medium text-slate-200 mb-2">
              Movie Title *
            </label>
            <Input
              type="text"
              name="title"
              value={formData.title}
              onChange={handleInputChange}
              placeholder="e.g., The Amazing Adventure"
              className="w-full"
            />
          </div>

          {/* Slug */}
          <div>
            <label className="block text-sm font-medium text-slate-200 mb-2">
              URL Slug *
            </label>
            <Input
              type="text"
              name="slug"
              value={formData.slug}
              onChange={handleInputChange}
              placeholder="e.g., the-amazing-adventure"
              className="w-full"
            />
          </div>

          {/* Description */}
          <div>
            <label className="block text-sm font-medium text-slate-200 mb-2">
              Description
            </label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              placeholder="Enter movie description..."
              rows="4"
              className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:border-indigo-500 resize-none"
            />
          </div>

          {/* Poster URL */}
          <div>
            <label className="block text-sm font-medium text-slate-200 mb-2">
              Poster URL
            </label>
            <Input
              type="url"
              name="posterUrl"
              value={formData.posterUrl}
              onChange={handleInputChange}
              placeholder="https://example.com/poster.jpg"
              className="w-full"
            />
          </div>

          {/* Duration and Release Date */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-slate-200 mb-2">
                Duration (minutes) *
              </label>
              <Input
                type="number"
                name="duration"
                value={formData.duration}
                onChange={handleInputChange}
                placeholder="e.g., 120"
                min="1"
                className="w-full"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-200 mb-2">
                Release Date *
              </label>
              <Input
                type="date"
                name="releaseDate"
                value={formData.releaseDate}
                onChange={handleInputChange}
                className="w-full"
              />
            </div>
          </div>

          {/* Language and Rating */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-slate-200 mb-2">
                Language
              </label>
              <Input
                type="text"
                name="language"
                value={formData.language}
                onChange={handleInputChange}
                placeholder="e.g., English"
                className="w-full"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-200 mb-2">
                Rating (0-10)
              </label>
              <Input
                type="number"
                name="rating"
                value={formData.rating}
                onChange={handleInputChange}
                placeholder="e.g., 8.5"
                min="0"
                max="10"
                step="0.1"
                className="w-full"
              />
            </div>
          </div>

          {/* Genres */}
          {genres.length > 0 && (
            <div>
              <label className="block text-sm font-medium text-slate-200 mb-3">
                Genres
              </label>
              <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                {genres.map((genre) => (
                  <label
                    key={genre.id}
                    className="flex items-center gap-2 cursor-pointer"
                  >
                    <input
                      type="checkbox"
                      checked={formData.genreIds.includes(genre.id)}
                      onChange={() => handleGenreChange(genre.id)}
                      className="w-4 h-4 rounded border-slate-600 text-indigo-600 focus:ring-indigo-500"
                    />
                    <span className="text-slate-300">{genre.name}</span>
                  </label>
                ))}
              </div>
            </div>
          )}

          {/* Actors */}
          {actors.length > 0 && (
            <div>
              <label className="block text-sm font-medium text-slate-200 mb-3">
                Actors
              </label>
              <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                {actors.map((actor) => (
                  <label
                    key={actor.id}
                    className="flex items-center gap-2 cursor-pointer"
                  >
                    <input
                      type="checkbox"
                      checked={formData.actorIds.includes(actor.id)}
                      onChange={() => handleActorChange(actor.id)}
                      className="w-4 h-4 rounded border-slate-600 text-indigo-600 focus:ring-indigo-500"
                    />
                    <span className="text-slate-300">{actor.name}</span>
                  </label>
                ))}
              </div>
            </div>
          )}

          {/* Submit and Cancel */}
          <div className="flex gap-4 pt-6 border-t border-slate-700">
            <Button
              type="submit"
              disabled={loading}
              className="flex-1 bg-indigo-600 hover:bg-indigo-700 text-white py-3 rounded-lg font-medium transition-colors disabled:opacity-50"
            >
              {loading ? (
                <>
                  <Loader className="w-4 h-4 animate-spin inline mr-2" />
                  Creating...
                </>
              ) : (
                "Create Movie"
              )}
            </Button>
            <Button
              type="button"
              onClick={() => navigate("/movies")}
              className="flex-1 bg-slate-700 hover:bg-slate-600 text-white py-3 rounded-lg font-medium transition-colors"
            >
              Cancel
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateMovie;
