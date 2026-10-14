import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import Slider from "react-slick";
import "./Hero.css";
import "slick-carousel/slick/slick.css";
import { ArrowRight, ChevronLeft, ChevronRight, Play, Star } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { useWatchlist } from "../../context/WatchlistContext";
import { usePopup } from "../../hooks/usePopup";
import ToWatchButton from "../buttons/ToWatchButton";
import WatchedButton from "../buttons/WatchedButton";
import Popup from "../common/Popup.jsx";
import LoadingSpinner from "../common/LoadingSpinner";
import TrailerModal from "./TrailerModal.jsx";
import {
  formatRating,
  formatRuntime,
  formatVoteCount,
  getBackdrop,
  getGenreNames,
  getTrailerSource,
  getYear,
  truncate,
} from "../../utils/movieUtils";

const AUTOPLAY_MS = 7000;

const prefersReducedMotion = () =>
    typeof window !== "undefined" &&
    window.matchMedia?.("(prefers-reduced-motion: reduce)").matches;

const Hero = ({ trendingMovies }) => {
  const { isWatched, isToWatch, toggleMovieStatus } = useWatchlist();
  const { popup, showPopup } = usePopup();
  const navigate = useNavigate();

  const sliderRef = useRef(null);
  const [current, setCurrent] = useState(0);
  const [hovering, setHovering] = useState(false);
  const [trailer, setTrailer] = useState(null); // { source, title }
  const reducedMotion = useMemo(prefersReducedMotion, []);

  // Optimistic state: "<listType>:<movieId>" -> boolean. While a request is
  // running it overrides what the context says, so the button reacts instantly.
  const [optimistic, setOptimistic] = useState({});

  const isOn = (listType, movieId) => {
    const override = optimistic[`${listType}:${movieId}`];
    if (override !== undefined) return override;
    return listType === "watched" ? isWatched(movieId) : isToWatch(movieId);
  };

  const handleWatchlistClick = async (movieId, listType) => {
    const key = `${listType}:${movieId}`;
    if (key in optimistic) return; // previous request for this button still running

    // The UI updates immediately, while the API request runs in the background.
    setOptimistic((prev) => ({ ...prev, [key]: !isOn(listType, movieId) }));

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
    } finally {
      // Drop the override: on success the context already holds the new state,
      // on error this reverts the button to the real (unchanged) state.
      setOptimistic(({ [key]: _done, ...rest }) => rest);
    }
  };

  const paused = hovering || Boolean(trailer);

  // Autoplay is driven manually so hover, focus and the trailer modal can pause it.
  useEffect(() => {
    if (reducedMotion) return;
    if (paused) sliderRef.current?.slickPause();
    else sliderRef.current?.slickPlay();
  }, [paused, reducedMotion]);

  const closeTrailer = useCallback(() => setTrailer(null), []);

  const settings = {
    dots: false,
    arrows: false,
    infinite: true,
    fade: true,
    speed: 900,
    slidesToShow: 1,
    slidesToScroll: 1,
    autoplay: !reducedMotion,
    autoplaySpeed: AUTOPLAY_MS,
    pauseOnHover: false,
    adaptiveHeight: false,
    accessibility: true,
    beforeChange: (_, next) => setCurrent(next),
  };

  if (!trendingMovies || trendingMovies.length === 0) {
    return (
        <div className="hero-loading">
          <LoadingSpinner message="Loading movies..." />
        </div>
    );
  }

  const total = trendingMovies.length;
  const pad = (n) => String(n).padStart(2, "0");

  return (
      <section
          className={`hero ${paused || reducedMotion ? "is-paused" : ""}`}
          aria-roledescription="carousel"
          aria-label="Trending movies"
          onMouseEnter={() => setHovering(true)}
          onMouseLeave={() => setHovering(false)}
          onFocus={() => setHovering(true)}
          onBlur={(e) => {
            if (!e.currentTarget.contains(e.relatedTarget)) setHovering(false);
          }}
      >
        <Popup popup={popup} />

        <Slider ref={sliderRef} {...settings}>
          {trendingMovies.map((movie, index) => {
            const backdrop = getBackdrop(movie);
            const rating = formatRating(movie.voteAverage);
            const votes = formatVoteCount(movie.voteCount);
            const year = getYear(movie.releaseDate);
            const runtime = formatRuntime(movie.runtime);
            const genres = getGenreNames(movie.genres, 3);
            const overview = truncate(movie.overview, 210);
            const tagline =
                movie.tagline && movie.tagline.length <= 90 ? movie.tagline : "";
            const trailerSource = getTrailerSource(movie.trailerUrl);
            const isActive = index === current;
            const hasMeta = rating || year || runtime;

            return (
                <div
                    key={movie.tmdbId || movie.imdbId || movie.title || index}
                    className="hero-slide-wrap"
                >
                  <article
                      className={`hero-slide ${isActive ? "is-active" : ""}`}
                      role="group"
                      aria-roledescription="slide"
                      aria-label={`${index + 1} of ${total}`}
                      {...(isActive ? {} : { inert: "" })}
                  >
                    {backdrop ? (
                        <img
                            className="hero-slide__backdrop"
                            src={backdrop}
                            alt=""
                            loading={index === 0 ? "eager" : "lazy"}
                            decoding="async"
                            onError={(e) => {
                              e.currentTarget.style.visibility = "hidden";
                            }}
                        />
                    ) : null}
                    <div className="hero-slide__shade" aria-hidden="true" />

                    <div className="hero-slide__content">
                      <div className="hero-copy">
                        {genres.length > 0 && (
                            <p className="hero-eyebrow">{genres.join("  ·  ")}</p>
                        )}

                        <h2
                            className={`hero-title ${
                                (movie.title || "").length > 28 ? "is-long" : ""
                            }`}
                        >
                          {movie.title}
                        </h2>

                        {tagline && <p className="hero-tagline">{tagline}</p>}

                        {hasMeta && (
                            <div className="hero-meta">
                              {rating && (
                                  <span
                                      className="hero-meta__rating"
                                      aria-label={`Rating ${rating} out of 10${
                                          votes ? `, ${votes} votes` : ""
                                      }`}
                                  >
                            <Star size={14} fill="currentColor" strokeWidth={0} />
                            <b>{rating}</b>
                            <span className="hero-meta__of">/10</span>
                                    {votes && (
                                        <span className="hero-meta__votes">{votes}</span>
                                    )}
                          </span>
                              )}
                              {year && <span>{year}</span>}
                              {runtime && <span>{runtime}</span>}
                            </div>
                        )}

                        {overview && <p className="hero-overview">{overview}</p>}

                        <div className="hero-actions">
                          <button
                              type="button"
                              className="hero-cta"
                              onClick={() => navigate(`/Details/${movie.tmdbId}`)}
                          >
                            View details
                            <ArrowRight size={16} strokeWidth={1.75} />
                          </button>

                          {trailerSource &&
                              (trailerSource.kind === "external" ? (
                                  <a
                                      className="hero-trailer"
                                      href={trailerSource.href}
                                      target="_blank"
                                      rel="noopener noreferrer"
                                  >
                                    <Play size={13} fill="currentColor" strokeWidth={0} />
                                    Watch trailer
                                  </a>
                              ) : (
                                  <button
                                      type="button"
                                      className="hero-trailer"
                                      onClick={() =>
                                          setTrailer({
                                            source: trailerSource,
                                            title: movie.title,
                                          })
                                      }
                                  >
                                    <Play size={13} fill="currentColor" strokeWidth={0} />
                                    Watch trailer
                                  </button>
                              ))}

                          <span className="hero-actions__rule" aria-hidden="true" />

                          <div className="hero-utilities">
                            <ToWatchButton
                                variant="icon"
                                movie={movie}
                                handleWatchlistClick={handleWatchlistClick}
                                isToWatch={(id) => isOn("toWatch", id)}
                            />
                            <WatchedButton
                                variant="icon"
                                movie={movie}
                                handleWatchlistClick={handleWatchlistClick}
                                isWatched={(id) => isOn("watched", id)}
                            />
                          </div>
                        </div>
                      </div>

                      {movie.posterPath && (
                          <div className="hero-poster" aria-hidden="true">
                            <img
                                src={movie.posterPath}
                                alt=""
                                loading={index === 0 ? "eager" : "lazy"}
                                decoding="async"
                                onError={(e) => {
                                  e.currentTarget.parentElement.style.display = "none";
                                }}
                            />
                          </div>
                      )}
                    </div>
                  </article>
                </div>
            );
          })}
        </Slider>

        {total > 1 && (
            <div className="hero-controls">
              <div className="hero-progress" role="group" aria-label="Choose slide">
                {trendingMovies.map((m, i) => (
                    <button
                        key={m.tmdbId || i}
                        type="button"
                        className={`hero-progress__seg ${i === current ? "is-current" : ""}`}
                        onClick={() => sliderRef.current?.slickGoTo(i)}
                        aria-label={`Go to slide ${i + 1}: ${m.title || ""}`}
                        aria-current={i === current ? "true" : undefined}
                    />
                ))}
              </div>
              <div className="hero-controls__nav">
            <span className="hero-counter" aria-hidden="true">
              {pad(current + 1)}
              <i> / </i>
              {pad(total)}
            </span>
                <button
                    type="button"
                    className="hero-arrow"
                    onClick={() => sliderRef.current?.slickPrev()}
                    aria-label="Previous movie"
                >
                  <ChevronLeft size={20} strokeWidth={1.5} />
                </button>
                <button
                    type="button"
                    className="hero-arrow"
                    onClick={() => sliderRef.current?.slickNext()}
                    aria-label="Next movie"
                >
                  <ChevronRight size={20} strokeWidth={1.5} />
                </button>
              </div>
            </div>
        )}

        {trailer && (
            <TrailerModal
                trailer={trailer.source}
                title={trailer.title}
                onClose={closeTrailer}
            />
        )}
      </section>
  );
};

export default Hero;