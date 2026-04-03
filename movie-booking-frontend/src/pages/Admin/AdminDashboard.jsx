import { useState } from "react";
import { Link, Outlet } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import {
  Film,
  BarChart3,
  Settings,
  LogOut,
  Menu,
  X,
  Plus,
  List,
  TrendingUp,
  Users,
  Tag,
} from "lucide-react";
import { Button } from "../../components/UI/Button";

const AdminDashboard = () => {
  const { logout, user } = useAuth();
  const [sidebarOpen, setSidebarOpen] = useState(true);

  const menuItems = [
    {
      icon: BarChart3,
      label: "Dashboard",
      path: "/admin/dashboard",
      badge: null,
    },
    {
      icon: Plus,
      label: "Create Movie",
      path: "/admin/movies/create",
      badge: "New",
    },
    {
      icon: List,
      label: "Manage Movies",
      path: "/admin/movies",
      badge: null,
    },
    {
      icon: Tag,
      label: "Manage Genres",
      path: "/admin/genres",
      badge: null,
    },
    {
      icon: Users,
      label: "Manage Actors",
      path: "/admin/actors",
      badge: null,
    },
    {
      icon: TrendingUp,
      label: "Analytics",
      path: "/admin/analytics",
      badge: null,
    },
    {
      icon: Settings,
      label: "Settings",
      path: "/admin/settings",
      badge: null,
    },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 to-slate-800">
      <div className="flex h-screen overflow-hidden">
        {/* Sidebar */}
        <aside
          className={`${
            sidebarOpen ? "w-64" : "w-20"
          } bg-slate-900 border-r border-slate-700 transition-all duration-300 flex flex-col`}
        >
          {/* Sidebar Header */}
          <div className="p-4 border-b border-slate-700 flex items-center justify-between">
            {sidebarOpen && (
              <div className="flex items-center gap-2">
                <Film className="w-6 h-6 text-indigo-500" />
                <span className="font-bold text-white text-lg">Admin</span>
              </div>
            )}
            <button
              onClick={() => setSidebarOpen(!sidebarOpen)}
              className="p-1 hover:bg-slate-800 rounded transition-colors"
            >
              {sidebarOpen ? (
                <X className="w-5 h-5 text-slate-400" />
              ) : (
                <Menu className="w-5 h-5 text-slate-400" />
              )}
            </button>
          </div>

          {/* Navigation Menu */}
          <nav className="flex-1 p-4 space-y-2 overflow-y-auto">
            {menuItems.map((item) => {
              const Icon = item.icon;
              return (
                <Link
                  key={item.path}
                  to={item.path}
                  className="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-slate-800 transition-colors text-slate-300 hover:text-white relative group"
                  title={!sidebarOpen ? item.label : ""}
                >
                  <Icon className="w-5 h-5 flex-shrink-0" />
                  {sidebarOpen && (
                    <>
                      <span className="font-medium">{item.label}</span>
                      {item.badge && (
                        <span className="ml-auto px-2 py-1 bg-indigo-600 text-xs font-bold text-white rounded">
                          {item.badge}
                        </span>
                      )}
                    </>
                  )}
                  {!sidebarOpen && item.badge && (
                    <span className="absolute -right-2 -top-2 w-2 h-2 bg-indigo-500 rounded-full" />
                  )}
                </Link>
              );
            })}
          </nav>

          {/* Sidebar Footer */}
          <div className="p-4 border-t border-slate-700">
            <button
              onClick={logout}
              className="w-full flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-red-600/20 transition-colors text-slate-300 hover:text-red-400"
            >
              <LogOut className="w-5 h-5 flex-shrink-0" />
              {sidebarOpen && <span className="font-medium">Logout</span>}
            </button>
          </div>
        </aside>

        {/* Main Content */}
        <main className="flex-1 flex flex-col overflow-hidden">
          {/* Top Bar */}
          <div className="bg-slate-800 border-b border-slate-700 px-6 py-4 flex items-center justify-between">
            <h1 className="text-2xl font-bold text-white">Admin Dashboard</h1>
            <div className="flex items-center gap-4">
              <div className="text-right">
                <p className="text-sm text-slate-400">Logged in as</p>
                <p className="font-semibold text-white">
                  {user?.preferred_username || "Admin"}
                </p>
              </div>
            </div>
          </div>

          {/* Content Area */}
          <div className="flex-1 overflow-y-auto p-6">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
};

export default AdminDashboard;
