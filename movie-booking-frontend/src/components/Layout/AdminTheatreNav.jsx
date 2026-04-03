import { useAuth } from "../../context/AuthContext";
import { Link } from "react-router-dom";
import {
  Film,
  Building2,
  BarChart3,
  Settings,
  ChevronDown,
} from "lucide-react";
import { useState } from "react";

const AdminTheatreNav = () => {
  const { userRoles } = useAuth();
  const [isOpen, setIsOpen] = useState(false);

  const isAdmin = userRoles.includes("ADMIN");
  const isTheatreOwner = userRoles.includes("THEATRE_OWNER");

  if (!isAdmin && !isTheatreOwner) {
    return null;
  }

  return (
    <div className="relative group">
      <button className="flex items-center gap-2 px-4 py-2 rounded-lg bg-indigo-600 hover:bg-indigo-700 text-white transition-colors">
        <Settings className="w-5 h-5" />
        <span>Admin Panel</span>
        <ChevronDown className="w-4 h-4 group-hover:rotate-180 transition-transform" />
      </button>

      {/* Dropdown Menu */}
      <div className="absolute right-0 mt-2 w-56 bg-slate-800 border border-slate-700 rounded-lg shadow-lg opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200 z-50">
        {/* Admin Section */}
        {isAdmin && (
          <div className="border-b border-slate-700 p-4">
            <h3 className="text-sm font-semibold text-indigo-400 mb-3 flex items-center gap-2">
              <Film className="w-4 h-4" />
              Admin Functions
            </h3>
            <Link
              to="/admin/movies/create"
              className="block px-3 py-2 rounded text-sm text-slate-300 hover:bg-slate-700 hover:text-white transition-colors"
            >
              + Create Movie
            </Link>
            <a
              href="#"
              className="block px-3 py-2 rounded text-sm text-slate-300 hover:bg-slate-700 hover:text-white transition-colors"
            >
              Manage Movies
            </a>
            <a
              href="#"
              className="block px-3 py-2 rounded text-sm text-slate-300 hover:bg-slate-700 hover:text-white transition-colors"
            >
              View Analytics
            </a>
          </div>
        )}

        {/* Theatre Owner Section */}
        {isTheatreOwner && (
          <div className="p-4">
            <h3 className="text-sm font-semibold text-purple-400 mb-3 flex items-center gap-2">
              <Building2 className="w-4 h-4" />
              Theatre Management
            </h3>
            <Link
              to="/theatre/create"
              className="block px-3 py-2 rounded text-sm text-slate-300 hover:bg-slate-700 hover:text-white transition-colors"
            >
              + Create Theatre
            </Link>
            <a
              href="#"
              className="block px-3 py-2 rounded text-sm text-slate-300 hover:bg-slate-700 hover:text-white transition-colors"
            >
              My Theatres
            </a>
            <a
              href="#"
              className="block px-3 py-2 rounded text-sm text-slate-300 hover:bg-slate-700 hover:text-white transition-colors"
            >
              Manage Shows
            </a>
            <a
              href="#"
              className="block px-3 py-2 rounded text-sm text-slate-300 hover:bg-slate-700 hover:text-white transition-colors"
            >
              Bookings & Revenue
            </a>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminTheatreNav;
