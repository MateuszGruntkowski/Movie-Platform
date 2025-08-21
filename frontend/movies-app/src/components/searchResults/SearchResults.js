import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { Container, Row, Col, Card, Spinner, Alert } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faVideoSlash } from "@fortawesome/free-solid-svg-icons";

const SearchResults = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);

  // Extract search query from URL parameters
  const searchParams = new URLSearchParams(location.search);
  const query = searchParams.get("q");

  // TMDB API configuration
  const TMDB_API_KEY = process.env.REACT_APP_TMDB_API_KEY;
  const TMDB_BASE_URL = "https://api.themoviedb.org/3";
  const TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w300";

  useEffect(() => {
    if (query) {
      searchMovies(query, currentPage);
    }
  }, [query, currentPage]);

  const searchMovies = async (searchQuery, page = 1) => {
    setLoading(true);
    setError(null);

    try {
      const options = {
        method: "GET",
        headers: {
          accept: "application/json",
          Authorization: `Bearer ${TMDB_API_KEY}`,
        },
      };

      const response = await fetch(
        `${TMDB_BASE_URL}/search/movie?query=${encodeURIComponent(
          searchQuery
        )}&language=en-US&page=${page}`,
        options
      );

      if (!response.ok) {
        throw new Error("Failed to fetch movies");
      }

      const data = await response.json();
      setMovies(data.results);
      setTotalPages(data.total_pages);
    } catch (error) {
      console.error("Error searching movies:", error);
      setError("Failed to search movies. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleMovieClick = (movie) => {
    navigate(`/Details/${movie.id}`);
  };

  if (!query) {
    return (
      <Container className="mt-5">
        <Alert variant="warning">
          No search query provided. Please use the search bar to find movies.
        </Alert>
      </Container>
    );
  }

  if (loading) {
    return (
      <Container className="mt-5 text-center">
        <Spinner animation="border" variant="warning" />
        <p className="mt-3">Searching for movies...</p>
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="mt-5">
        <Alert variant="danger">{error}</Alert>
      </Container>
    );
  }

  return (
    <Container className="mt-4">
      <Row>
        <Col>
          <h2 className="mb-4 text-white">
            Search Results for "{query}"
            <span className="text-muted ms-2">
              ({movies.length} results found)
            </span>
          </h2>
        </Col>
      </Row>

      {movies.length === 0 ? (
        <Row>
          <Col>
            <Alert variant="info">
              No movies found for "{query}". Try searching with different
              keywords.
            </Alert>
          </Col>
        </Row>
      ) : (
        <Row>
          {movies.map((movie) => (
            <Col key={movie.id} xs={6} sm={4} md={3} lg={2} className="mb-4">
              <Card
                className="movie-search-card h-100"
                onClick={() => handleMovieClick(movie)}
                style={{
                  cursor: "pointer",
                  backgroundColor: "#2c3e50",
                  border: "1px solid rgba(255, 255, 255, 0.1)",
                  transition: "transform 0.3s ease, box-shadow 0.3s ease",
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.transform = "translateY(-5px)";
                  e.currentTarget.style.boxShadow =
                    "0 8px 25px rgba(0, 0, 0, 0.3)";
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.transform = "translateY(0)";
                  e.currentTarget.style.boxShadow = "none";
                }}
              >
                <div style={{ height: "300px", overflow: "hidden" }}>
                  {movie.poster_path ? (
                    <Card.Img
                      variant="top"
                      src={`${TMDB_IMAGE_BASE_URL}${movie.poster_path}`}
                      alt={movie.title}
                      style={{
                        height: "100%",
                        objectFit: "cover",
                      }}
                      onError={(e) => {
                        e.target.src =
                          "https://via.placeholder.com/300x450/cccccc/666666?text=No+Image";
                      }}
                    />
                  ) : (
                    <div
                      className="d-flex align-items-center justify-content-center"
                      style={{
                        height: "100%",
                        backgroundColor: "rgba(255, 255, 255, 0.1)",
                        color: "rgba(255, 255, 255, 0.5)",
                      }}
                    >
                      <FontAwesomeIcon icon={faVideoSlash} size="3x" />
                    </div>
                  )}
                </div>
                <Card.Body className="p-2">
                  <Card.Title
                    className="text-white mb-1"
                    style={{
                      fontSize: "14px",
                      lineHeight: "1.3",
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                      whiteSpace: "nowrap",
                    }}
                    title={movie.title}
                  >
                    {movie.title}
                  </Card.Title>
                  <Card.Text
                    className="text-muted mb-1"
                    style={{ fontSize: "12px" }}
                  >
                    {movie.release_date
                      ? new Date(movie.release_date).getFullYear()
                      : "N/A"}
                  </Card.Text>
                  <div
                    style={{
                      fontSize: "12px",
                      color: "#ffd700",
                    }}
                  >
                    ⭐{" "}
                    {movie.vote_average ? movie.vote_average.toFixed(1) : "N/A"}
                  </div>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      )}

      {/* Pagination can be added here if needed */}
      {totalPages > 1 && (
        <Row className="mt-4">
          <Col className="text-center">
            <p className="text-muted">
              Page {currentPage} of {totalPages}
            </p>
            {/* Add pagination controls here if needed */}
          </Col>
        </Row>
      )}
    </Container>
  );
};

export default SearchResults;
