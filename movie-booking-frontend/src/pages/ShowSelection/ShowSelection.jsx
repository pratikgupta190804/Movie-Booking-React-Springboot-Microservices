import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { MapPin, Calendar, Clock } from "lucide-react";
import { Card, CardBody } from "../../components/UI/Card";
import { Button } from "../../components/UI/Button";
import { Loader } from "../../components/UI/Loader";
import { showService } from "../../services/showService";
import { theatreService } from "../../services/theatreService";
import toast from "react-hot-toast";

const ShowSelection = () => {
  const { movieId } = useParams();
  const navigate = useNavigate();
  const [shows, setShows] = useState([]);
  const [theatres, setTheatres] = useState({});
  const [loading, setLoading] = useState(true);
  const [selectedDate, setSelectedDate] = useState(
    new Date().toISOString().split("T")[0],
  );

  useEffect(() => {
    fetchShows();
  }, [movieId]);

  const fetchShows = async () => {
    try {
      setLoading(true);

      // Fetch shows for the movie
      const showsData = await showService.getShowsByMovie(movieId);
      console.log("Fetched shows:", showsData);

      // Filter by selected date
      setShows(showsData);

      // Fetch theatre details for each show
      const theatreIds = [...new Set(showsData.map((s) => s.theatreId))];
      const theatrePromises = theatreIds.map((id) =>
        theatreService.getTheatreById(id),
      );
      const theatreResults = await Promise.all(theatrePromises);

      const theatreMap = {};
      theatreResults.forEach((theatre) => {
        theatreMap[theatre.id] = theatre;
      });

      
      setTheatres(theatreMap);
    } catch (error) {
      console.error("Error fetching shows:", error);
      toast.error("Failed to load shows");

      // Demo data fallback
      setShows([
        {
          id: 1,
          movieId,
          theatreId: 1,
          screenId: 1,
          showTime: `${selectedDate}T10:00:00`,
          price: 200,
        },
        {
          id: 2,
          movieId,
          theatreId: 1,
          screenId: 2,
          showTime: `${selectedDate}T14:00:00`,
          price: 250,
        },
        {
          id: 3,
          movieId,
          theatreId: 2,
          screenId: 3,
          showTime: `${selectedDate}T18:00:00`,
          price: 300,
        },
      ]);

      setTheatres({
        1: {
          id: 1,
          name: "PVR Cinemas",
          location: "Phoenix Mall",
          city: "Mumbai",
        },
        2: { id: 2, name: "INOX", location: "R City Mall", city: "Mumbai" },
      });
    } finally {
      setLoading(false);
    }
  };

  const handleShowSelect = (showId) => {
    navigate(`/shows/${showId}/seats`);
  };

  const filteredShows = shows.filter((show) => {
    const showDate = new Date(show.startTime).toISOString().split("T")[0];
    return showDate === selectedDate;
  });

  const groupShowsByTheatre = () => {
    const grouped = {};
    filteredShows.forEach((show) => {
      if (!grouped[show.theatreId]) {
        grouped[show.theatreId] = [];
      }
      grouped[show.theatreId].push(show);
    });
    return grouped;
  };

  const formatTime = (dateTime) => {
    return new Date(dateTime).toLocaleTimeString("en-US", {
      hour: "2-digit",
      minute: "2-digit",
      hour12: true,
    });
  };

  // Generate next 7 days
  const dates = [];
  for (let i = 0; i < 7; i++) {
    const date = new Date();
    date.setDate(date.getDate() + i);
    dates.push(date.toISOString().split("T")[0]);
  }

  const groupedShows = groupShowsByTheatre();

  return (
    <div className="min-h-screen bg-primary-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-4xl font-bold text-primary-900 mb-8">
          Select Show
        </h1>

        {/* Date Selector */}
        <Card className="mb-8">
          <CardBody>
            <div className="flex items-center gap-2 mb-4">
              <Calendar className="h-5 w-5 text-accent" />
              <span className="font-semibold text-primary-900">
                Select Date
              </span>
            </div>
            <div className="flex gap-3 overflow-x-auto scrollbar-hide pb-2">
              {dates.map((date) => {
                const dateObj = new Date(date);
                const isSelected = date === selectedDate;
                return (
                  <button
                    key={date}
                    onClick={() => setSelectedDate(date)}
                    className={`
                      flex flex-col items-center px-6 py-3 rounded-lg border-2 min-w-[100px]
                      transition-all
                      ${
                        isSelected
                          ? "bg-accent text-white border-accent"
                          : "bg-white text-primary-700 border-primary-300 hover:border-accent"
                      }
                    `}
                  >
                    <span className="text-sm font-medium">
                      {dateObj.toLocaleDateString("en-US", {
                        weekday: "short",
                      })}
                    </span>
                    <span className="text-2xl font-bold">
                      {dateObj.getDate()}
                    </span>
                    <span className="text-sm">
                      {dateObj.toLocaleDateString("en-US", { month: "short" })}
                    </span>
                  </button>
                );
              })}
            </div>
          </CardBody>
        </Card>

        {/* Shows List */}
        {loading ? (
          <Loader />
        ) : Object.keys(groupedShows).length === 0 ? (
          <Card>
            <CardBody className="text-center py-12">
              <p className="text-xl text-primary-600">
                No shows available for the selected date
              </p>
            </CardBody>
          </Card>
        ) : (
          <div className="space-y-6">
            {Object.entries(groupedShows).map(([theatreId, theatreShows]) => {
              const theatre = theatres[theatreId] || {};
              return (
                <Card key={theatreId}>
                  <CardBody>
                    <div className="mb-4">
                      <h3 className="text-xl font-bold text-primary-900 mb-2">
                        {theatre.name || "Theatre"}
                      </h3>
                      <div className="flex items-center gap-2 text-primary-600">
                        <MapPin className="h-4 w-4" />
                        <span>
                          {theatre.location || "Location"},{" "}
                          {theatre.city || "City"}
                        </span>
                      </div>
                    </div>

                    <div className="flex flex-wrap gap-3">
                      {theatreShows.map((show) => (
                        <button
                          key={show.id}
                          onClick={() => handleShowSelect(show.id)}
                          className="flex flex-col items-center px-6 py-3 bg-primary-50 hover:bg-accent hover:text-white border-2 border-primary-200 hover:border-accent rounded-lg transition-all group"
                        >
                          <div className="flex items-center gap-2 mb-1">
                            <Clock className="h-4 w-4" />
                            <span className="font-semibold text-lg">
                              {formatTime(show.startTime)}
                            </span>
                          </div>
                          <span className="text-sm opacity-75">
                            ₹
                            {show.price ?? show.seatPrices?.[0]?.price ?? "N/A"}
                          </span>
                        </button>
                      ))}
                    </div>
                  </CardBody>
                </Card>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};

export default ShowSelection;
