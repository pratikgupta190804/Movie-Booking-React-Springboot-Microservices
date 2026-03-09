import { Link, Outlet } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { Film, User, LogOut, Menu, X } from "lucide-react";
import { useState } from "react";
import { Button } from "../UI/Button";

const Layout = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <div className="min-h-screen flex flex-col">
      {/* Header */}
      <header className="bg-white shadow-md sticky top-0 z-40">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            {/* Logo */}
            <Link to="/" className="flex items-center gap-2">
              <Film className="h-8 w-8 text-accent" />
              <span className="text-2xl font-bold text-primary-900">
                Movie<span className="text-accent">Booking</span>
              </span>
            </Link>

            {/* Desktop Navigation */}
            <nav className="hidden md:flex items-center gap-6">
              <Link
                to="/"
                className="text-primary-700 hover:text-accent transition-colors font-medium"
              >
                Home
              </Link>
              <Link
                to="/movies"
                className="text-primary-700 hover:text-accent transition-colors font-medium"
              >
                Movies
              </Link>
            </nav>

            {/* User Menu */}
            <div className="hidden md:flex items-center gap-4">
              {isAuthenticated ? (
                <>
                  <div className="flex items-center gap-2 text-primary-700">
                    <User className="h-5 w-5" />
                    <span className="font-medium">
                      {user?.preferred_username || user?.name || "User"}
                    </span>
                  </div>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={logout}
                    className="flex items-center gap-2"
                  >
                    <LogOut className="h-4 w-4" />
                    Logout
                  </Button>
                </>
              ) : (
                <>
                  <Link to="/login">
                    <Button variant="ghost" size="sm">
                      Login
                    </Button>
                  </Link>
                  <Link to="/register">
                    <Button variant="primary" size="sm">
                      Sign Up
                    </Button>
                  </Link>
                </>
              )}
            </div>

            {/* Mobile menu button */}
            <button
              className="md:hidden text-primary-700"
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            >
              {mobileMenuOpen ? (
                <X className="h-6 w-6" />
              ) : (
                <Menu className="h-6 w-6" />
              )}
            </button>
          </div>

          {/* Mobile Navigation */}
          {mobileMenuOpen && (
            <div className="md:hidden py-4 border-t border-primary-200">
              <nav className="flex flex-col gap-4">
                <Link
                  to="/"
                  className="text-primary-700 hover:text-accent transition-colors font-medium"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Home
                </Link>
                <Link
                  to="/movies"
                  className="text-primary-700 hover:text-accent transition-colors font-medium"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Movies
                </Link>
                {isAuthenticated ? (
                  <>
                    <div className="flex items-center gap-2 text-primary-700">
                      <User className="h-5 w-5" />
                      <span className="font-medium">
                        {user?.preferred_username || user?.name || "User"}
                      </span>
                    </div>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => {
                        logout();
                        setMobileMenuOpen(false);
                      }}
                      className="flex items-center gap-2 justify-start"
                    >
                      <LogOut className="h-4 w-4" />
                      Logout
                    </Button>
                  </>
                ) : (
                  <div className="flex gap-2">
                    <Link to="/login" className="flex-1">
                      <Button
                        variant="ghost"
                        size="sm"
                        className="w-full"
                        onClick={() => setMobileMenuOpen(false)}
                      >
                        Login
                      </Button>
                    </Link>
                    <Link to="/register" className="flex-1">
                      <Button
                        variant="primary"
                        size="sm"
                        className="w-full"
                        onClick={() => setMobileMenuOpen(false)}
                      >
                        Sign Up
                      </Button>
                    </Link>
                  </div>
                )}
              </nav>
            </div>
          )}
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1">
        <Outlet />
      </main>

      {/* Footer */}
      <footer className="bg-primary-900 text-white py-8 mt-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex flex-col md:flex-row justify-between items-center gap-4">
            <div className="flex items-center gap-2">
              <Film className="h-6 w-6 text-accent" />
              <span className="text-lg font-bold">MovieBooking</span>
            </div>
            <p className="text-primary-300 text-sm">
              © 2024 MovieBooking. All rights reserved.
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Layout;
