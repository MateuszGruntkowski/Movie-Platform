import React from "react";
import SearchResultItem from "./SearchResultItem";

const MAX_VISIBLE = 8;

const SearchResultsDropdown = ({ results, query, onMovieClick, onViewAll }) => {
    if (results.length === 0) {
        return query.length >= 2 ? (
            <div className="no-results">
                <p>No movies found for "{query}"</p>
            </div>
        ) : null;
    }

    const visibleResults = results.slice(0, MAX_VISIBLE);
    const hasMore = results.length > MAX_VISIBLE;

    return (
        <>
            <div className="search-results">
                {visibleResults.map((movie) => (
                    <SearchResultItem key={movie.id} movie={movie} onClick={onMovieClick} />
                ))}
            </div>

            {(hasMore || query) && (
                <div className="search-footer">
                    <button className="view-all-button" onClick={onViewAll}>
                        {hasMore
                            ? `View all ${results.length} results for "${query}"`
                            : `View all results for "${query}"`}
                    </button>
                </div>
            )}
        </>
    );
};

export default SearchResultsDropdown;