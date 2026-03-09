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
