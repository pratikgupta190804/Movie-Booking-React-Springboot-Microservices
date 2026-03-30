import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Mail, Lock, Film } from "lucide-react";
import { useAuth } from "../../context/AuthContext";
import { Input } from "../../components/UI/Input";
import { Button } from "../../components/UI/Button";
import { Card, CardBody } from "../../components/UI/Card";

const Login = () => {
  const navigate = useNavigate();
  const { login, isAuthenticated } = useAuth();
  const [formData, setFormData] = useState({
    username: "",
    password: "",
  });
  const [loading, setLoading] = useState(false);

  const handleGoogleLogin = () => {
    // Redirect to Keycloak's Google login URL
    const keycloakGoogleUrl =
      `http://localhost:8181/realms/movie-booking-app/protocol/openid-connect/auth` +
      `?client_id=movie-booking-client` +
      `&redirect_uri=${encodeURIComponent("http://localhost:3000/auth/callback")}` +
      `&response_type=code` +
      `&scope=openid email profile` +
      `&kc_idp_hint=google`; // ← this tells Keycloak to go straight to Google

    window.location.href = keycloakGoogleUrl;
  };

  // Redirect if already logged in
  if (isAuthenticated) {
    navigate("/");
    return null;
  }

  const handleChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    const success = await login(formData.username, formData.password);

    setLoading(false);

    if (success) {
      navigate("/");
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-900 via-primary-800 to-primary-900 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full">
        {/* Logo */}
        <div className="text-center mb-8">
          <Link to="/" className="inline-flex items-center gap-2">
            <Film className="h-12 w-12 text-accent" />
            <span className="text-4xl font-bold text-white">
              Movie<span className="text-accent">Booking</span>
            </span>
          </Link>
          <p className="mt-4 text-lg text-primary-200">
            Sign in to book your tickets
          </p>
        </div>

        <Card>
          <CardBody className="p-8">
            <form onSubmit={handleSubmit} className="space-y-6">
              <Input
                label="Username or Email"
                type="text"
                name="username"
                value={formData.username}
                onChange={handleChange}
                placeholder="Enter your username"
                icon={Mail}
                required
              />

              <Input
                label="Password"
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="Enter your password"
                icon={Lock}
                required
              />

              <div className="flex items-center justify-between text-sm">
                <label className="flex items-center gap-2 text-primary-700">
                  <input type="checkbox" className="rounded" />
                  <span>Remember me</span>
                </label>
                <a
                  href="#"
                  className="text-accent hover:text-accent-dark font-medium"
                >
                  Forgot password?
                </a>
              </div>

              <Button
                type="submit"
                variant="primary"
                size="lg"
                className="w-full"
                loading={loading}
              >
                Sign In
              </Button>

              <div className="text-center text-sm text-primary-600">
                Don't have an account?{" "}
                <Link
                  to="/register"
                  className="text-accent hover:text-accent-dark font-medium"
                >
                  Sign up
                </Link>
              </div>
            </form>

            {/* Google Login Button */}
            <button
              onClick={handleGoogleLogin}
              className="w-full flex items-center justify-center gap-3
                     border border-primary-200 rounded-xl py-3
                     hover:bg-primary-50 transition-all duration-200
                     text-primary-700 font-medium"
            >
              {/* Google SVG Icon */}
              <svg width="20" height="20" viewBox="0 0 48 48">
                <path
                  fill="#EA4335"
                  d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38
                 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43
                 13.72 17.74 9.5 24 9.5z"
                />
                <path
                  fill="#4285F4"
                  d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58
                 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36
                 7.09-17.65z"
                />
                <path
                  fill="#FBBC05"
                  d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l
                 -7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56
                 10.78l7.97-6.19z"
                />
                <path
                  fill="#34A853"
                  d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15
                 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l
                 -7.98 6.19C6.51 42.62 14.62 48 24 48z"
                />
              </svg>
              Continue with Google
            </button>

            {/* Demo credentials info */}
            <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
              <p className="text-sm text-blue-800 font-medium mb-2">
                For testing purposes:
              </p>
              <p className="text-xs text-blue-700">
                Configure Keycloak or use demo mode in AuthService
              </p>
            </div>
          </CardBody>
        </Card>
      </div>
    </div>
  );
};

export default Login;
