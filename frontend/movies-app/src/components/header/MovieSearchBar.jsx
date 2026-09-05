import React, { useState, useRef, useEffect } from "react";
import { Form } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTimes } from "@fortawesome/free-solid-svg-icons";
import { useNavigate } from "react-router-dom";
import { useMovieSearch } from "./useMovieSearch";
import SearchResultsDropdown from "./SearchResultDropdown";
import "./MovieSearchBar.css";

const MovieSearchBar = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const searchRef = useRef(null);
  const navigate = useNavigate();
  const { results, isSearching, showResults, setShowResults } = useMovieSearch(searchQuery);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (searchRef.current && !searchRef.current.contains(event.target)) {
        setShowResults(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [setShowResults]);

  const handleMovieClick = (movie) => {
    setShowResults(false);
    setSearchQuery("");
    navigate(`/Details/${movie.id}`);
  };

  const handleViewAllResults = () => {
    navigate(`/search?q=${encodeURIComponent(searchQuery)}`);
    setShowResults(false);
    setSearchQuery("");
  };

  const clearSearch = () => {
    setSearchQuery("");
    setShowResults(false);
  };

  return (
      <div className="movie-search-container" ref={searchRef}>
        <div className="search-input-wrapper">
          <Form.Control
              type="text"
              placeholder="Search movies..."
              className="movie-search-input"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onFocus={() => searchQuery.length >= 2 && setShowResults(true)}
          />
          {searchQuery && (
              <button type="button" className="clear-search-button" onClick={clearSearch} aria-label="Clear search">
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
              <SearchResultsDropdown
                  results={results}
                  query={searchQuery}
                  onMovieClick={handleMovieClick}
                  onViewAll={handleViewAllResults}
              />
            </div>
        )}
      </div>
  );
};

export default MovieSearchBar;