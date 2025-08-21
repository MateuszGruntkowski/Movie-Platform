import React, { useState, useEffect, useRef } from "react";
import { Form } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faVideoSlash, faTimes } from "@fortawesome/free-solid-svg-icons";
import { useNavigate } from "react-router-dom";
import { movieSearchService } from "../../Services/movieSearchService";
import "./MovieSearchBar.css";

const MovieSearchBar = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [isSearching, setIsSearching] = useState(false);
  const [showResults, setShowResults] = useState(false);
  const searchRef = useRef(null);
  const navigate = useNavigate();

  // Debounce search
  useEffect(() => {
    const timeoutId = setTimeout(() => {
      if (searchQuery.trim().length >= 2) {
        performSearch(searchQuery);
      } else {
        setSearchResults([]);
        setShowResults(false);
      }
    }, 300);

    return () => clearTimeout(timeoutId);
  }, [searchQuery]);

  // Close on click outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (searchRef.current && !searchRef.current.contains(event.target)) {
        setShowResults(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const performSearch = async (query) => {
    setIsSearching(true);
    try {
      const results = await movieSearchService.searchMovies(query);
      setSearchResults(results);
      setShowResults(true);
    } catch (error) {
      setSearchResults([]);
      setShowResults(false);
    } finally {
      setIsSearching(false);
    }
  };

  const handleInputChange = (e) => {
    setSearchQuery(e.target.value);
  };

  const handleMovieClick = (movie) => {
    setShowResults(false);
    setSearchQuery("");
    navigate(`/Details/${movie.imdbId || movie.id}`);
  };

  const clearSearch = () => {
    setSearchQuery("");
    setSearchResults([]);
    setShowResults(false);
  };

  const handleViewAllResults = () => {
    navigate(`/search?q=${encodeURIComponent(searchQuery)}`);
    setShowResults(false);
    setSearchQuery("");
  };

  return (
    <div className="movie-search-container" ref={searchRef}>
      <div className="search-input-wrapper">
        <Form.Control
          type="text"
          placeholder="Search movies..."
          className="movie-search-input"
          value={searchQuery}
          onChange={handleInputChange}
          onFocus={() => searchQuery.length >= 2 && setShowResults(true)}
        />

        {searchQuery && (
          <button
            type="button"
            className="clear-search-button"
            onClick={clearSearch}
            aria-label="Clear search"
          >
            <FontAwesomeIcon icon={faTimes} />
          </button>
        )}

        {isSearching && (
          <div className="search-spinner">
            <div className="spinner-border spinner-border-sm" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
          </div>
        )}
      </div>

      {showResults && (
        <div className="search-dropdown">
          {searchResults.length > 0 ? (
            <>
              <div className="search-results">
                {searchResults.map((movie) => (
                  <div
                    key={movie.id}
                    className="search-result-item"
                    onClick={() => handleMovieClick(movie)}
                  >
                    <div className="result-poster">
                      {movie.posterUrl ? (
                        <img
                          src={movie.posterUrl}
                          alt={movie.title}
                          onError={(e) => {
                            e.target.src =
                              "https://via.placeholder.com/92x138/cccccc/666666?text=No+Image";
                          }}
                        />
                      ) : (
                        <div className="no-poster">
                          <FontAwesomeIcon icon={faVideoSlash} />
                        </div>
                      )}
                    </div>
                    <div className="result-info">
                      <h6 className="result-title">{movie.title}</h6>
                      <p className="result-year">
                        {movie.releaseDate
                          ? new Date(movie.releaseDate).getFullYear()
                          : "N/A"}
                      </p>
                      <div className="result-rating">
                        ⭐{" "}
                        {movie.voteAverage
                          ? movie.voteAverage.toFixed(1)
                          : "N/A"}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
              {searchQuery && (
                <div className="search-footer">
                  <button
                    className="view-all-button"
                    onClick={handleViewAllResults}
                  >
                    View all results for "{searchQuery}"
                  </button>
                </div>
              )}
            </>
          ) : (
            searchQuery.length >= 2 &&
            !isSearching && (
              <div className="no-results">
                <p>No movies found for "{searchQuery}"</p>
              </div>
            )
          )}
        </div>
      )}
    </div>
  );
};

export default MovieSearchBar;
