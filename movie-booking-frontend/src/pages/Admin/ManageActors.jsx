import { useState, useEffect } from "react";
import { Users, Edit2, Trash2, Plus, Loader, AlertCircle } from "lucide-react";
import { Button } from "../../components/UI/Button";
import { movieService } from "../../services/movieService";
import toast from "react-hot-toast";

const ActorManagement = () => {
  const [actors, setActors] = useState([]);
  const [newActor, setNewActor] = useState({
    name: "",
    bio: "",
    profileImage: "",
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editingId, setEditingId] = useState(null);
  const [editValue, setEditValue] = useState("");

  useEffect(() => {
    const fetchActors = async () => {
      try {
        setLoading(true);
        const response = await movieService.getAllActors();
        const actorsList = Array.isArray(response) ? response : [];
        setActors(actorsList);
        setLoading(false);
      } catch (err) {
        console.error("Error fetching actors:", err);
        setError("Failed to fetch actors");
        toast.error("Failed to load actors");
        setLoading(false);
      }
    };

    fetchActors();
  }, []);

  const handleAddActor = async (e) => {
    e.preventDefault();

    if (!newActor.name.trim()) {
      toast.error("Please enter actor name");
      return;
    }

    try {
      const actorData = {
        name: newActor.name,
        bio: newActor.bio || "",
        profileImage: newActor.profileImage || "",
      };

      // Note: Add create method that returns the created actor
      // const created = await movieService.createActor(actorData);
      // setActors([...actors, created]);
      toast.success("Actor added successfully!");
      setNewActor({ name: "", bio: "", profileImage: "" });
    } catch (error) {
      console.error("Error adding actor:", error);
      toast.error("Failed to add actor");
    }
  };

  const handleDeleteActor = async (actorId) => {
    if (!window.confirm("Are you sure you want to delete this actor?")) {
      return;
    }

    try {
      // Note: Add delete method to movieService
      // await movieService.deleteActor(actorId);
      toast.success("Actor deleted successfully!");
      setActors(actors.filter((a) => a.id !== actorId));
    } catch (error) {
      console.error("Error deleting actor:", error);
      toast.error("Failed to delete actor");
    }
  };

  const handleEditActor = async (actorId) => {
    if (!editValue.trim()) {
      toast.error("Please enter actor name");
      return;
    }

    try {
      // Note: Add update method to movieService
      // await movieService.updateActor(actorId, { name: editValue });
      toast.success("Actor updated successfully!");
      setActors(
        actors.map((a) => (a.id === actorId ? { ...a, name: editValue } : a)),
      );
      setEditingId(null);
      setEditValue("");
    } catch (error) {
      console.error("Error updating actor:", error);
      toast.error("Failed to update actor");
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
    <div className="max-w-6xl mx-auto px-4 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-white mb-2">Actor Management</h1>
        <p className="text-slate-400">Add and manage movie actors</p>
      </div>

      {error && (
        <div className="mb-6 bg-red-500/20 border border-red-600 rounded-lg p-4 flex items-start gap-3">
          <AlertCircle className="w-5 h-5 text-red-400 flex-shrink-0 mt-0.5" />
          <p className="text-red-300">{error}</p>
        </div>
      )}

      {/* Add Actor Form */}
      <div className="bg-slate-800 border border-slate-700 rounded-lg p-6 mb-8">
        <h2 className="text-xl font-bold text-white mb-4">Add New Actor</h2>
        <form onSubmit={handleAddActor} className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <input
              type="text"
              value={newActor.name}
              onChange={(e) =>
                setNewActor({ ...newActor, name: e.target.value })
              }
              placeholder="Actor name"
              className="px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:border-indigo-500"
            />
            <input
              type="url"
              value={newActor.profileImage}
              onChange={(e) =>
                setNewActor({ ...newActor, profileImage: e.target.value })
              }
              placeholder="Profile image URL"
              className="px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:border-indigo-500"
            />
          </div>
          <textarea
            value={newActor.bio}
            onChange={(e) => setNewActor({ ...newActor, bio: e.target.value })}
            placeholder="Bio/Description (optional)"
            rows="3"
            className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:border-indigo-500 resize-none"
          />
          <Button
            type="submit"
            className="bg-indigo-600 hover:bg-indigo-700 text-white font-medium px-6 py-2 rounded-lg flex items-center gap-2 transition-colors"
          >
            <Plus className="w-4 h-4" />
            Add Actor
          </Button>
        </form>
      </div>

      {/* Actors Grid */}
      <div className="bg-slate-800 border border-slate-700 rounded-lg p-6">
        <h2 className="text-xl font-bold text-white mb-6">Actors</h2>

        {actors.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {actors.map((actor) => (
              <div
                key={actor.id}
                className="bg-slate-700/50 border border-slate-600 rounded-lg overflow-hidden hover:border-indigo-500/50 transition-colors"
              >
                {actor.profileImage && (
                  <div className="h-48 bg-slate-600 overflow-hidden">
                    <img
                      src={actor.profileImage}
                      alt={actor.name}
                      className="w-full h-full object-cover"
                      onError={(e) => (e.target.style.display = "none")}
                    />
                  </div>
                )}
                <div className="p-4">
                  {editingId === actor.id ? (
                    <input
                      type="text"
                      value={editValue}
                      onChange={(e) => setEditValue(e.target.value)}
                      className="w-full px-2 py-1 bg-slate-600 border border-slate-500 rounded text-white focus:outline-none focus:border-indigo-500 mb-2"
                      autoFocus
                    />
                  ) : (
                    <h3 className="font-bold text-white mb-2">{actor.name}</h3>
                  )}

                  {actor.bio && (
                    <p className="text-sm text-slate-400 mb-4 line-clamp-2">
                      {actor.bio}
                    </p>
                  )}

                  <div className="flex items-center gap-2">
                    {editingId === actor.id ? (
                      <>
                        <button
                          onClick={() => handleEditActor(actor.id)}
                          className="flex-1 px-3 py-1 bg-green-600/20 hover:bg-green-600/30 text-green-300 text-xs font-bold rounded transition-colors"
                        >
                          Save
                        </button>
                        <button
                          onClick={() => setEditingId(null)}
                          className="flex-1 px-3 py-1 bg-slate-600 hover:bg-slate-500 text-slate-300 text-xs font-bold rounded transition-colors"
                        >
                          Cancel
                        </button>
                      </>
                    ) : (
                      <>
                        <button
                          onClick={() => {
                            setEditingId(actor.id);
                            setEditValue(actor.name);
                          }}
                          className="p-2 bg-blue-600/20 hover:bg-blue-600/30 text-blue-400 rounded transition-colors"
                        >
                          <Edit2 className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => handleDeleteActor(actor.id)}
                          className="p-2 bg-red-600/20 hover:bg-red-600/30 text-red-400 rounded transition-colors"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-12">
            <Users className="w-12 h-12 text-slate-600 mx-auto mb-4" />
            <p className="text-slate-400 font-medium">No actors yet</p>
            <p className="text-slate-500 text-sm">
              Add your first actor using the form above
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ActorManagement;
