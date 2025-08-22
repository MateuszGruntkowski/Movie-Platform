// SearchResultsHeader.js
import React from "react";

const SearchResultsHeader = ({ query, resultsCount }) => {
  return (
    <div className="sr-header">
      <h1>Search Results</h1>
      <div className="sr-search-info">
        <span className="sr-query">"{query}"</span>
        <span className="sr-count">({resultsCount} results found)</span>
      </div>
    </div>
  );
};

export default SearchResultsHeader;
