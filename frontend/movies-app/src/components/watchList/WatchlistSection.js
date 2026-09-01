import React from "react";
import MovieCard from "./MovieCard";

const WatchlistSection = ({
                              title,
                              icon,
                              listType,
                              state,
                              onLoadMore,
                              onMarkAsWatched,
                              onMarkAsToWatch,
                              onRemove,
                              emptyMessage,
                              emptySubMessage,
                          }) => {
    const { movies, isLast, isLoading } = state;

    return (
        <section className="wl-movies-section">
            <div className="wl-section-header">
                <h2>
                    {icon}
                    {title}
                </h2>
                <span className="wl-count">{movies.length}</span>
            </div>

            <div className="wl-movies-grid">
                {movies.length > 0 ? (
                    movies.map((movie) => (
                        <MovieCard
                            key={movie.tmdbId}
                            movie={movie}
                            listType={listType}
                            onMarkAsWatched={onMarkAsWatched}
                            onMarkAsToWatch={onMarkAsToWatch}
                            onRemove={onRemove}
                        />
                    ))
                ) : !isLoading ? (
                    <div className="wl-empty-state">
                        {icon}
                        <p>{emptyMessage}</p>
                        <small>{emptySubMessage}</small>
                    </div>
                ) : null}
            </div>

            {!isLast && (
                <div className="wl-section-footer">
                    <button
                        className="wl-load-more-btn"
                        onClick={onLoadMore}
                        disabled={isLoading}
                    >
                        {isLoading ? "Loading..." : "Load more"}
                    </button>
                </div>
            )}
        </section>
    );
};

export default WatchlistSection;