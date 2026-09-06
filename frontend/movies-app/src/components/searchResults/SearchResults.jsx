import { useState, useEffect, useCallback } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { Spinner, Alert } from "react-bootstrap";
import { movieSearchService } from "../../services/movieSearchService";
import SearchResultsHeader from "./SearchResultsHeader";
import SearchResultsGrid from "./SearchResultsGrid";
import SearchResultsPagination from "./SearchResultsPagination";
import "./SearchResults.css";

const SearchResults = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [totalResults, setTotalResults] = useState(0);

  const searchParams = new URLSearchParams(location.search);
  const query = searchParams.get("q");

  const searchMovies = useCallback(async (searchQuery, page = 1) => {
    setLoading(true);
    setError(null);

    try {
      const data = await movieSearchService.searchMovies(searchQuery, page);
      setMovies(data.results);
      setTotalPages(data.totalPages);
      setTotalResults(data.totalResults);
    } catch (error) {
      console.error("Error searching movies:", error);
      setError("Failed to search movies. Please try again.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (query) {
      searchMovies(query, currentPage);
    }
  }, [query, currentPage, searchMovies]);

  const handleMovieClick = (movie) => {
    navigate(`/Details/${movie.id}`);
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  if (!query) {
    return (
        <div className="sr-container">
          <Alert variant="warning">
            No search query provided. Please use the search bar to find movies.
          </Alert>
        </div>
    );
  }

  if (loading) {
    return (
        <div className="sr-container">
          <div className="sr-loading">
            <Spinner animation="border" variant="warning" />
            <p>Searching for movies...</p>
          </div>
        </div>
    );
  }

  if (error) {
    return (
        <div className="sr-container">
          <Alert variant="danger">{error}</Alert>
        </div>
    );
  }

  return (
      <div className="sr-container">
        <div className="sr-content">
          <SearchResultsHeader query={query} resultsCount={totalResults} />
          <SearchResultsGrid
              movies={movies}
              onMovieClick={handleMovieClick}
              query={query}
          />
          <SearchResultsPagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={handlePageChange}
          />
        </div>
      </div>
  );
};

export default SearchResults;