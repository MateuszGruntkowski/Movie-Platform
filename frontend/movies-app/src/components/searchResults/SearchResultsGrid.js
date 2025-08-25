import React from "react";
import SearchMovieCard from "./SearchMovieCard";

const SearchResultsGrid = ({ movies, onMovieClick, query }) => {
  if (movies.length === 0) {
    return (
      <div className="sr-empty-state">
        <p>No movies found for "{query}"</p>
        <small>Try searching with different keywords.</small>
      </div>
    );
  }

  return (
    <div className="sr-movies-grid">
      {movies.map((movie) => (
        <SearchMovieCard
          key={movie.id}
          movie={movie}
          onMovieClick={onMovieClick}
        />
      ))}
    </div>
  );
};

export default SearchResultsGrid;
