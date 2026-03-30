// src/pages/Auth/AuthCallback.jsx
import { useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

const AuthCallback = () => {
  const navigate = useNavigate();
  const { handleOAuthCallback } = useAuth();
  const hasRun = useRef(false);   // ← prevents double execution in StrictMode

  useEffect(() => {
    // ── Guard against React StrictMode double-firing ───────────────
    // StrictMode runs effects twice in dev — authorization code
    // is single-use so second attempt always fails with "Code not valid"
    if (hasRun.current) return;
    hasRun.current = true;

    const urlParams = new URLSearchParams(window.location.search);
    const code  = urlParams.get("code");
    const error = urlParams.get("error");

    if (error) {
      console.error("OAuth error:", error);
      navigate("/login", { replace: true });
      return;
    }

    if (!code) {
      navigate("/login", { replace: true });
      return;
    }

    handleOAuthCallback(code)
      .then(() => navigate("/", { replace: true }))
      .catch((err) => {
        console.error("Login failed:", err);
        navigate("/login", { replace: true });
      });

  }, []);   // ← empty deps — only run once on mount

  return (
    <div className="min-h-screen bg-primary-50 flex items-center justify-center">
      <div className="text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2
                        border-indigo-600 mx-auto mb-4" />
        <p className="text-primary-600 font-medium">Completing sign in...</p>
        <p className="text-primary-400 text-sm mt-2">Please wait</p>
      </div>
    </div>
  );
};

export default AuthCallback;