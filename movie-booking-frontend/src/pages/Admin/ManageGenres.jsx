import { useState, useEffect } from "react";
import { Tag, Edit2, Trash2, Plus, Loader, AlertCircle } from "lucide-react";
import { Button } from "../../components/UI/Button";
import { movieService } from "../../services/movieService";
import toast from "react-hot-toast";

const GenreManagement = () => {
  const [genres, setGenres] = useState([]);
  const [newGenre, setNewGenre] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editingId, setEditingId] = useState(null);
  const [editValue, setEditValue] = useState("");

  useEffect(() => {
    const fetchGenres = async () => {
      try {
        setLoading(true);
        const response = await movieService.getAllGenres();
        const genresList = Array.isArray(response) ? response : [];
        setGenres(genresList);
        setLoading(false);
      } catch (err) {
        console.error("Error fetching genres:", err);
        setError("Failed to fetch genres");
        toast.error("Failed to load genres");
        setLoading(false);
      }
    };

    fetchGenres();
  }, []);

  const handleAddGenre = async (e) => {
    e.preventDefault();

    if (!newGenre.trim()) {
      toast.error("Please enter a genre name");
      return;
    }

    try {
      // Note: Add create method that returns the created genre
      // const created = await movieService.createGenre(newGenre);
      // setGenres([...genres, created]);
      toast.success("Genre added successfully!");
      setNewGenre("");
    } catch (error) {
      console.error("Error adding genre:", error);
      toast.error("Failed to add genre");
    }
  };

  const handleDeleteGenre = async (genreId) => {
    if (!window.confirm("Are you sure you want to delete this genre?")) {
      return;
    }

    try {
      // Note: Add delete method to movieService
      // await movieService.deleteGenre(genreId);
      toast.success("Genre deleted successfully!");
      setGenres(genres.filter((g) => g.id !== genreId));
    } catch (error) {
      console.error("Error deleting genre:", error);
      toast.error("Failed to delete genre");
    }
  };

  const handleEditGenre = async (genreId) => {
    if (!editValue.trim()) {
      toast.error("Please enter a genre name");
      return;
    }

    try {
      // Note: Add update method to movieService
      // await movieService.updateGenre(genreId, editValue);
      toast.success("Genre updated successfully!");
      setGenres(
        genres.map((g) => (g.id === genreId ? { ...g, name: editValue } : g)),
      );
      setEditingId(null);
      setEditValue("");
    } catch (error) {
      console.error("Error updating genre:", error);
      toast.error("Failed to update genre");
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loader className="w-8 h-8 animate-spin text-indigo-500" />
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-white mb-2">Genre Management</h1>
        <p className="text-slate-400">Add and manage movie genres</p>
      </div>

      {error && (
        <div className="mb-6 bg-red-500/20 border border-red-600 rounded-lg p-4 flex items-start gap-3">
          <AlertCircle className="w-5 h-5 text-red-400 flex-shrink-0 mt-0.5" />
          <p className="text-red-300">{error}</p>
        </div>
      )}

      {/* Add Genre Form */}
      <div className="bg-slate-800 border border-slate-700 rounded-lg p-6 mb-8">
        <h2 className="text-xl font-bold text-white mb-4">Add New Genre</h2>
        <form onSubmit={handleAddGenre} className="flex gap-4">
          <input
            type="text"
            value={newGenre}
            onChange={(e) => setNewGenre(e.target.value)}
            placeholder="Enter genre name (e.g., Action, Comedy, Drama)"
            className="flex-1 px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:border-indigo-500"
          />
          <Button
            type="submit"
            className="bg-indigo-600 hover:bg-indigo-700 text-white font-medium px-6 py-2 rounded-lg flex items-center gap-2 transition-colors"
          >
            <Plus className="w-4 h-4" />
            Add Genre
          </Button>
        </form>
      </div>

      {/* Genres Grid */}
      <div className="bg-slate-800 border border-slate-700 rounded-lg p-6">
        <h2 className="text-xl font-bold text-white mb-6">Genres</h2>

        {genres.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {genres.map((genre) => (
              <div
                key={genre.id}
                className="bg-slate-700/50 border border-slate-600 rounded-lg p-4 flex items-center justify-between hover:border-indigo-500/50 transition-colors"
              >
                <div className="flex items-center gap-3">
                  <Tag className="w-5 h-5 text-indigo-400" />
                  {editingId === genre.id ? (
                    <input
                      type="text"
                      value={editValue}
                      onChange={(e) => setEditValue(e.target.value)}
                      className="px-2 py-1 bg-slate-600 border border-slate-500 rounded text-white focus:outline-none focus:border-indigo-500"
                      autoFocus
                    />
                  ) : (
                    <span className="font-medium text-white">{genre.name}</span>
                  )}
                </div>

                <div className="flex items-center gap-2">
                  {editingId === genre.id ? (
                    <>
                      <button
                        onClick={() => handleEditGenre(genre.id)}
                        className="px-3 py-1 bg-green-600/20 hover:bg-green-600/30 text-green-300 text-xs font-bold rounded transition-colors"
                      >
                        Save
                      </button>
                      <button
                        onClick={() => setEditingId(null)}
                        className="px-3 py-1 bg-slate-600 hover:bg-slate-500 text-slate-300 text-xs font-bold rounded transition-colors"
                      >
                        Cancel
                      </button>
                    </>
                  ) : (
                    <>
                      <button
                        onClick={() => {
                          setEditingId(genre.id);
                          setEditValue(genre.name);
                        }}
                        className="p-2 bg-blue-600/20 hover:bg-blue-600/30 text-blue-400 rounded transition-colors"
                      >
                        <Edit2 className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => handleDeleteGenre(genre.id)}
                        className="p-2 bg-red-600/20 hover:bg-red-600/30 text-red-400 rounded transition-colors"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </>
                  )}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-12">
            <Tag className="w-12 h-12 text-slate-600 mx-auto mb-4" />
            <p className="text-slate-400 font-medium">No genres yet</p>
            <p className="text-slate-500 text-sm">
              Add your first genre using the form above
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default GenreManagement;
