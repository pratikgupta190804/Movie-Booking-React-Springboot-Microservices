import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { Loader } from "../UI/Loader";
import { AlertCircle } from "lucide-react";

/**
 * ProtectedRoute component with optional role-based access control
 * @param {React.ReactNode} children - Route component
 * @param {string | string[]} requiredRoles - Optional role(s) required to access the route
 */
const ProtectedRoute = ({ children, requiredRoles }) => {
  const { isAuthenticated, loading, user } = useAuth();

  if (loading) {
    return <Loader fullScreen />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Check role-based access if required
  if (requiredRoles) {
    const userRoles = extractUserRoles(user);
    const rolesArray = Array.isArray(requiredRoles)
      ? requiredRoles
      : [requiredRoles];

    const hasRequiredRole = rolesArray.some((role) =>
      userRoles.includes(role.toUpperCase()),
    );

    if (!hasRequiredRole) {
      return (
        <div className="min-h-screen bg-gradient-to-br from-slate-900 to-slate-800 flex items-center justify-center px-4">
          <div className="bg-slate-800 border border-red-500 rounded-lg p-8 max-w-md w-full">
            <div className="flex items-center gap-3 mb-4">
              <AlertCircle className="w-6 h-6 text-red-500" />
              <h1 className="text-xl font-bold text-red-500">Access Denied</h1>
            </div>
            <p className="text-slate-300 mb-6">
              You do not have permission to access this page. Required role(s):{" "}
              <span className="font-semibold text-red-400">
                {rolesArray.join(", ")}
              </span>
            </p>
            <a
              href="/"
              className="inline-block px-6 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg transition-colors"
            >
              Go Home
            </a>
          </div>
        </div>
      );
    }
  }

  return children;
};

/**
 * Extract roles from JWT token stored in user object
 * Roles are decoded from the realm_access claim in the JWT
 */
export const extractUserRoles = (user) => {
  if (!user) return [];

  // Try to get roles from different possible locations
  // 1. From decoded JWT token if available
  const token = localStorage.getItem("token");
  if (token) {
    try {
      const decoded = parseJwt(token);
      if (decoded.realm_access?.roles) {
        return decoded.realm_access.roles.map((role) => role.toUpperCase());
      }
    } catch (e) {
      console.warn("Failed to decode JWT token");
    }
  }

  // 2. Fallback: try resource access if available
  if (user.resource_access) {
    const roles = [];
    Object.values(user.resource_access).forEach((resource) => {
      if (resource.roles) {
        roles.push(...resource.roles);
      }
    });
    return roles.map((role) => role.toUpperCase());
  }

  return [];
};

/**
 * Helper function to decode JWT token
 */
const parseJwt = (token) => {
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join(""),
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    return null;
  }
};

export default ProtectedRoute;
