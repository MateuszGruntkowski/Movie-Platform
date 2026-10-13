import { useRef, useState } from "react";
import { ExternalLink, Play, Star } from "lucide-react";
import { useWatchlist } from "../../context/WatchlistContext";
import ToWatchButton from "../buttons/ToWatchButton";
import WatchedButton from "../buttons/WatchedButton";
import TrailerModal from "./TrailerModal";
import {
    formatRating,
    formatRuntime,
    formatVoteCount,
    getBackdrop,
    getGenreNames,
    getTrailerSource,
    getYear,
} from "../../utils/movieUtils.js";
import "./MovieHero.css";

const TrailerLabel = () => (
    <>
        <span className="hero-trailer-circle">
            <Play size={18} fill="currentColor" strokeWidth={0} aria-hidden="true" />
        </span>
        <span>Trailer</span>
    </>
);

const MovieHero = ({ movie, isLoading, showPopup }) => {
    const { toggleMovieStatus, isWatched, isToWatch } = useWatchlist();
    const [trailerOpen, setTrailerOpen] = useState(false);
    const [backdropFailed, setBackdropFailed] = useState(false);
    const [posterFailed, setPosterFailed] = useState(false);
    const trailerButtonRef = useRef(null);

    if (!movie) {
        return (
            <section className="movie-hero movie-hero--empty" aria-busy={isLoading}>
                {isLoading && <p className="movie-hero-status">Loading…</p>}
            </section>
        );
    }

    const handleWatchlistClick = async (movieId, listType) => {
        try {
            const isNowInList = await toggleMovieStatus(movieId, listType);
            const action = isNowInList ? "added" : "removed";
            const listName = listType === "watched" ? "watched" : "watch list";
            showPopup?.(
                `Movie ${action} ${isNowInList ? "to" : "from"} ${listName}!`,
                listType
            );
        } catch (error) {
            if (error.message === "NOT_AUTHENTICATED") {
                showPopup?.("Log in to add to watchlist!", "login");
            } else {
                console.error("Error toggling movie status:", error);
                showPopup?.("Something went wrong!", "error");
            }
        }
    };

    const closeTrailer = () => {
        setTrailerOpen(false);
        trailerButtonRef.current?.focus();
    };

    const backdrop = getBackdrop(movie);
    const trailer = getTrailerSource(movie.trailerUrl);
    const rating = formatRating(movie.voteAverage);
    const votes = formatVoteCount(movie.voteCount);
    const genres = getGenreNames(movie.genres, 4);
    const extraGenres = Math.max(
        (Array.isArray(movie.genres) ? movie.genres.length : 0) - genres.length,
        0
    );
    const metaItems = [
        getYear(movie.releaseDate),
        formatRuntime(movie.runtime),
        movie.adult ? "18+" : null,
    ].filter(Boolean);

    return (
        <section className="movie-hero">
            {backdrop && !backdropFailed && (
                <div className="movie-hero-backdrop" aria-hidden="true">
                    <img src={backdrop} alt="" onError={() => setBackdropFailed(true)} />
                </div>
            )}

            <div className="movie-hero-grid">
                <div className="movie-hero-poster">
                    {movie.posterPath && !posterFailed ? (
                        <img
                            src={movie.posterPath}
                            alt={`${movie.title || "Movie"} poster`}
                            onError={() => setPosterFailed(true)}
                        />
                    ) : (
                        <div className="movie-hero-poster-empty">No poster</div>
                    )}
                </div>

                <header className="movie-hero-head">
                    <h1 className="movie-hero-title">{movie.title}</h1>
                    {movie.tagline && <p className="movie-hero-tagline">{movie.tagline}</p>}
                    {metaItems.length > 0 && (
                        <p className="movie-hero-meta">
                            {metaItems.map((item, i) => (
                                <span key={item}>
                                    {i > 0 && <span className="movie-hero-sep" aria-hidden="true">·</span>}
                                    {item}
                                </span>
                            ))}
                        </p>
                    )}
                    {genres.length > 0 && (
                        <ul className="movie-hero-genres">
                            {genres.map((name) => (
                                <li key={name} className="movie-hero-genre">{name}</li>
                            ))}
                            {extraGenres > 0 && (
                                <li className="movie-hero-genre movie-hero-genre--more">
                                    +{extraGenres} more
                                </li>
                            )}
                        </ul>
                    )}
                </header>

                <div className="movie-hero-body">
                    {rating && (
                        <p className="movie-hero-rating">
                            <Star size={18} fill="currentColor" strokeWidth={0} aria-hidden="true" />
                            <span className="movie-hero-rating-value">{rating}</span>
                            <span className="movie-hero-rating-scale">/ 10</span>
                            {votes && <span className="movie-hero-votes">{votes} votes</span>}
                        </p>
                    )}

                    {movie.overview && <p className="movie-hero-overview">{movie.overview}</p>}

                    <div className="movie-hero-actions">
                        {trailer &&
                            (trailer.kind === "external" ? (
                                <a
                                    className="hero-trailer"
                                    href={trailer.href}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    aria-label="Watch trailer"
                                    title="Watch trailer"
                                >
                                    <TrailerLabel />
                                </a>
                            ) : (
                                <button
                                    ref={trailerButtonRef}
                                    type="button"
                                    className="hero-trailer"
                                    onClick={() => setTrailerOpen(true)}
                                    aria-label="Watch trailer"
                                    title="Watch trailer"
                                >
                                    <TrailerLabel />
                                </button>
                            ))}

                        <ToWatchButton
                            movie={movie}
                            variant="icon"
                            isToWatch={isToWatch}
                            handleWatchlistClick={handleWatchlistClick}
                        />
                        <WatchedButton
                            movie={movie}
                            variant="icon"
                            isWatched={isWatched}
                            handleWatchlistClick={handleWatchlistClick}
                        />

                        {movie.imdbId && (
                            <a
                                className="hero-link"
                                href={`https://www.imdb.com/title/${movie.imdbId}/`}
                                target="_blank"
                                rel="noopener noreferrer"
                            >
                                IMDb
                                <ExternalLink size={14} aria-hidden="true" />
                            </a>
                        )}
                    </div>
                </div>
            </div>

            {trailerOpen && trailer && trailer.kind !== "external" && (
                <TrailerModal source={trailer} title={movie.title} onClose={closeTrailer} />
            )}
        </section>
    );
};

export default MovieHero;