// src/App.jsx
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import { Toaster } from "react-hot-toast";
import { AuthProvider } from "./context/AuthContext";
import Layout from "./components/Layout/Layout";
import Home from "./pages/Home/Home";
import Movies from "./pages/Movies/Movies";
import MovieDetail from "./pages/MovieDetail/MovieDetail";
import ShowSelection from "./pages/ShowSelection/ShowSelection";
import SeatSelection from "./pages/SeatSelection/SeatSelection";
import BookingConfirmation from "./pages/BookingConfirmation/BookingConfirmation";
import PaymentPage from "./pages/PaymentPage/PaymentPage";
import PaymentHistory from "./pages/PaymentHistory/PaymentHistory";
import Login from "./pages/Auth/Login";
import Register from "./pages/Auth/Register";
import ProtectedRoute from "./components/ProtectedRoute/ProtectedRoute";
import MyTickets from "./pages/Ticket/MyTickets";
import TicketDetail from "./pages/Ticket/TicketDetail";
import TicketByBooking from "./pages/Ticket/TicketByBooking";
import AuthCallback from "./pages/Auth/AuthCallback";
import CreateMovie from "./pages/Admin/CreateMovie";
import CreateTheatre from "./pages/TheatreOwner/CreateTheatre";
import AdminDashboard from "./pages/Admin/AdminDashboard";
import AdminDashboardHome from "./pages/Admin/AdminDashboardHome";
import TheatreOwnerDashboard from "./pages/TheatreOwner/TheatreOwnerDashboard";
import TheatreOwnerDashboardHome from "./pages/TheatreOwner/TheatreOwnerDashboardHome";
import ManageMovies from "./pages/Admin/ManageMovies";
import ManageGenres from "./pages/Admin/ManageGenres";
import ManageActors from "./pages/Admin/ManageActors";
import CreateShow from "./pages/TheatreOwner/CreateShow";
import ManageShows from "./pages/TheatreOwner/ManageShows";

function App() {
  return (
    <AuthProvider>
      <Router>
        <Toaster
          position="top-right"
          toastOptions={{
            duration: 3000,
            style: {
              background: "#1e293b",
              color: "#fff",
            },
            success: {
              iconTheme: {
                primary: "#10b981",
                secondary: "#fff",
              },
            },
            error: {
              iconTheme: {
                primary: "#ef4444",
                secondary: "#fff",
              },
            },
          }}
        />
        <Routes>
          <Route path="/auth/callback" element={<AuthCallback />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/" element={<Layout />}>
            <Route index element={<Home />} />
            <Route path="movies" element={<Movies />} />
            <Route path="movies/:movieId" element={<MovieDetail />} />
            <Route path="movies/:movieId/shows" element={<ShowSelection />} />
            <Route
              path="shows/:showId/seats"
              element={
                <ProtectedRoute>
                  <SeatSelection />
                </ProtectedRoute>
              }
            />
            <Route
              path="booking/payment"
              element={
                <ProtectedRoute>
                  <PaymentPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="booking/confirmation"
              element={
                <ProtectedRoute>
                  <BookingConfirmation />
                </ProtectedRoute>
              }
            />
            <Route
              path="my-tickets"
              element={
                <ProtectedRoute>
                  <MyTickets />
                </ProtectedRoute>
              }
            />
            <Route
              path="ticket/:ticketId"
              element={
                <ProtectedRoute>
                  <TicketDetail />
                </ProtectedRoute>
              }
            />
            <Route
              path="ticket/booking/:bookingId"
              element={
                <ProtectedRoute>
                  <TicketByBooking />
                </ProtectedRoute>
              }
            />
            <Route
              path="payment/history"
              element={
                <ProtectedRoute>
                  <PaymentHistory />
                </ProtectedRoute>
              }
            />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Route>

          {/* Admin Dashboard Routes */}
          <Route
            path="/admin"
            element={
              <ProtectedRoute requiredRoles="ADMIN">
                <AdminDashboard />
              </ProtectedRoute>
            }
          >
            <Route index element={<Navigate to="dashboard" replace />} />
            <Route path="dashboard" element={<AdminDashboardHome />} />
            <Route path="movies/create" element={<CreateMovie />} />
            <Route path="movies/:movieId/edit" element={<CreateMovie />} />
            <Route path="movies" element={<ManageMovies />} />
            <Route path="genres" element={<ManageGenres />} />
            <Route path="actors" element={<ManageActors />} />
          </Route>

          {/* Theatre Owner Dashboard Routes */}
          <Route
            path="/theatre"
            element={
              <ProtectedRoute requiredRoles="THEATRE_OWNER">
                <TheatreOwnerDashboard />
              </ProtectedRoute>
            }
          >
            <Route index element={<Navigate to="dashboard" replace />} />
            <Route path="dashboard" element={<TheatreOwnerDashboardHome />} />
            <Route path="create" element={<CreateTheatre />} />
            <Route path="shows/create" element={<CreateShow />} />
            <Route path="shows" element={<ManageShows />} />
          </Route>
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
