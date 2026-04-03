import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  Building2,
  AlertCircle,
  Loader,
  MapPin,
  Phone,
  Mail,
} from "lucide-react";
import { useAuth } from "../../context/AuthContext";
import { theatreService } from "../../services/theatreService";
import { Button } from "../../components/UI/Button";
import { Input } from "../../components/UI/Input";
import toast from "react-hot-toast";

const CreateTheatre = () => {
  const navigate = useNavigate();
  const { hasRole, loading: authLoading } = useAuth();
  const [formData, setFormData] = useState({
    name: "",
    brand: "",
    description: "",
    addressLine1: "",
    addressLine2: "",
    city: "",
    state: "",
    country: "",
    postalCode: "",
    latitude: "",
    longitude: "",
    contactNumber: "",
    email: "",
    openingTime: "10:00",
    closingTime: "23:00",
    foodCourtAvailable: false,
    parkingAvailable: false,
    wheelchairAccessible: false,
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Redirect if not theatre owner
  useEffect(() => {
    if (!authLoading && !hasRole("THEATRE_OWNER")) {
      navigate("/");
      toast.error("You do not have permission to create theatres");
    }
  }, [authLoading, hasRole, navigate]);

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      // Validate required fields
      const required = [
        "name",
        "brand",
        "addressLine1",
        "city",
        "state",
        "country",
        "postalCode",
        "latitude",
        "longitude",
        "contactNumber",
        "email",
      ];

      const missingFields = required.filter((field) => !formData[field]);
      if (missingFields.length > 0) {
        setError(
          `Please fill in all required fields: ${missingFields.join(", ")}`,
        );
        setLoading(false);
        return;
      }

      const theatrePayload = {
        name: formData.name,
        brand: formData.brand,
        description: formData.description,
        addressLine1: formData.addressLine1,
        addressLine2: formData.addressLine2,
        city: formData.city,
        state: formData.state,
        country: formData.country,
        postalCode: formData.postalCode,
        latitude: parseFloat(formData.latitude),
        longitude: parseFloat(formData.longitude),
        contactNumber: formData.contactNumber,
        email: formData.email,
        openingTime: formData.openingTime,
        closingTime: formData.closingTime,
        foodCourtAvailable: formData.foodCourtAvailable,
        parkingAvailable: formData.parkingAvailable,
        wheelchairAccessible: formData.wheelchairAccessible,
      };

      const response = await theatreService.createTheatre(theatrePayload);
      toast.success("Theatre created successfully!");
      navigate(`/theatres/${response.id}`);
    } catch (err) {
      const errorMessage =
        err.response?.data?.message ||
        err.message ||
        "Failed to create theatre";
      setError(errorMessage);
      toast.error(errorMessage);
      console.error("Error creating theatre:", err);
    } finally {
      setLoading(false);
    }
  };

  if (authLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Loader className="w-8 h-8 animate-spin" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 to-slate-800 pt-20 pb-10">
      <div className="max-w-5xl mx-auto px-4">
        {/* Header */}
        <div className="mb-8">
          <div className="flex items-center gap-3 mb-4">
            <Building2 className="w-8 h-8 text-purple-500" />
            <h1 className="text-4xl font-bold text-white">
              Create New Theatre
            </h1>
          </div>
          <p className="text-slate-400">
            Register your theatre. Only theatre owners can create theatres.
          </p>
        </div>

        {/* Error Alert */}
        {error && (
          <div className="mb-6 p-4 bg-red-500/20 border border-red-500 rounded-lg flex items-start gap-3">
            <AlertCircle className="w-5 h-5 text-red-500 mt-0.5 flex-shrink-0" />
            <p className="text-red-200">{error}</p>
          </div>
        )}

        {/* Form */}
        <form
          onSubmit={handleSubmit}
          className="bg-slate-800 border border-slate-700 rounded-lg p-8 space-y-8"
        >
          {/* Basic Information */}
          <div>
            <h2 className="text-xl font-semibold text-white mb-4">
              Basic Information
            </h2>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-200 mb-2">
                    Theatre Name *
                  </label>
                  <Input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleInputChange}
                    placeholder="e.g., Grand Cinema Palace"
                    className="w-full"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-200 mb-2">
                    Brand/Chain Name *
                  </label>
                  <Input
                    type="text"
                    name="brand"
                    value={formData.brand}
                    onChange={handleInputChange}
                    placeholder="e.g., PVR, INOX, Cinepolis"
                    className="w-full"
                  />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-200 mb-2">
                  Description
                </label>
                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  placeholder="Enter theatre description..."
                  rows="3"
                  className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:border-purple-500 resize-none"
                />
              </div>
            </div>
          </div>

          {/* Address Information */}
          <div>
            <div className="flex items-center gap-2 mb-4">
              <MapPin className="w-5 h-5 text-purple-500" />
              <h2 className="text-xl font-semibold text-white">
                Address Information
              </h2>
            </div>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-200 mb-2">
                  Address Line 1 *
                </label>
                <Input
                  type="text"
                  name="addressLine1"
                  value={formData.addressLine1}
                  onChange={handleInputChange}
                  placeholder="Street address"
                  className="w-full"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-200 mb-2">
                  Address Line 2
                </label>
                <Input
                  type="text"
                  name="addressLine2"
                  value={formData.addressLine2}
                  onChange={handleInputChange}
                  placeholder="Apartment, suite, etc."
                  className="w-full"
                />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-200 mb-2">
                    City *
                  </label>
                  <Input
                    type="text"
                    name="city"
                    value={formData.city}
                    onChange={handleInputChange}
                    placeholder="e.g., Mumbai"
                    className="w-full"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-200 mb-2">
                    State *
                  </label>
                  <Input
                    type="text"
                    name="state"
                    value={formData.state}
                    onChange={handleInputChange}
                    placeholder="e.g., Maharashtra"
                    className="w-full"
                  />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-200 mb-2">
                    Country *
                  </label>
                  <Input
                    type="text"
                    name="country"
                    value={formData.country}
                    onChange={handleInputChange}
                    placeholder="e.g., India"
                    className="w-full"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-200 mb-2">
                    Postal Code *
                  </label>
                  <Input
                    type="text"
                    name="postalCode"
                    value={formData.postalCode}
                    onChange={handleInputChange}
                    placeholder="e.g., 400001"
                    className="w-full"
                  />
                </div>
              </div>
            </div>
          </div>

          {/* Geographic Coordinates */}
          <div>
            <h2 className="text-xl font-semibold text-white mb-4">
              Geographic Location
            </h2>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-200 mb-2">
                  Latitude *
                </label>
                <Input
                  type="number"
                  name="latitude"
                  value={formData.latitude}
                  onChange={handleInputChange}
                  placeholder="e.g., 19.0760"
                  step="0.000001"
                  className="w-full"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-200 mb-2">
                  Longitude *
                </label>
                <Input
                  type="number"
                  name="longitude"
                  value={formData.longitude}
                  onChange={handleInputChange}
                  placeholder="e.g., 72.8777"
                  step="0.000001"
                  className="w-full"
                />
              </div>
            </div>
          </div>

          {/* Contact Information */}
          <div>
            <div className="flex items-center gap-2 mb-4">
              <Phone className="w-5 h-5 text-purple-500" />
              <h2 className="text-xl font-semibold text-white">
                Contact Information
              </h2>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-200 mb-2">
                  Contact Number *
                </label>
                <Input
                  type="tel"
                  name="contactNumber"
                  value={formData.contactNumber}
                  onChange={handleInputChange}
                  placeholder="e.g., +91 9876543210"
                  className="w-full"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-200 mb-2">
                  Email *
                </label>
                <Input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleInputChange}
                  placeholder="e.g., info@theatre.com"
                  className="w-full"
                />
              </div>
            </div>
          </div>

          {/* Operating Hours */}
          <div>
            <h2 className="text-xl font-semibold text-white mb-4">
              Operating Hours
            </h2>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-200 mb-2">
                  Opening Time
                </label>
                <Input
                  type="time"
                  name="openingTime"
                  value={formData.openingTime}
                  onChange={handleInputChange}
                  className="w-full"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-200 mb-2">
                  Closing Time
                </label>
                <Input
                  type="time"
                  name="closingTime"
                  value={formData.closingTime}
                  onChange={handleInputChange}
                  className="w-full"
                />
              </div>
            </div>
          </div>

          {/* Amenities */}
          <div>
            <h2 className="text-xl font-semibold text-white mb-4">Amenities</h2>
            <div className="space-y-3">
              <label className="flex items-center gap-3 cursor-pointer">
                <input
                  type="checkbox"
                  name="foodCourtAvailable"
                  checked={formData.foodCourtAvailable}
                  onChange={handleInputChange}
                  className="w-4 h-4 rounded border-slate-600 text-purple-600 focus:ring-purple-500"
                />
                <span className="text-slate-300">Food Court Available</span>
              </label>
              <label className="flex items-center gap-3 cursor-pointer">
                <input
                  type="checkbox"
                  name="parkingAvailable"
                  checked={formData.parkingAvailable}
                  onChange={handleInputChange}
                  className="w-4 h-4 rounded border-slate-600 text-purple-600 focus:ring-purple-500"
                />
                <span className="text-slate-300">Parking Available</span>
              </label>
              <label className="flex items-center gap-3 cursor-pointer">
                <input
                  type="checkbox"
                  name="wheelchairAccessible"
                  checked={formData.wheelchairAccessible}
                  onChange={handleInputChange}
                  className="w-4 h-4 rounded border-slate-600 text-purple-600 focus:ring-purple-500"
                />
                <span className="text-slate-300">Wheelchair Accessible</span>
              </label>
            </div>
          </div>

          {/* Submit and Cancel */}
          <div className="flex gap-4 pt-6 border-t border-slate-700">
            <Button
              type="submit"
              disabled={loading}
              className="flex-1 bg-purple-600 hover:bg-purple-700 text-white py-3 rounded-lg font-medium transition-colors disabled:opacity-50"
            >
              {loading ? (
                <>
                  <Loader className="w-4 h-4 animate-spin inline mr-2" />
                  Creating...
                </>
              ) : (
                "Create Theatre"
              )}
            </Button>
            <Button
              type="button"
              onClick={() => navigate("/")}
              className="flex-1 bg-slate-700 hover:bg-slate-600 text-white py-3 rounded-lg font-medium transition-colors"
            >
              Cancel
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateTheatre;
