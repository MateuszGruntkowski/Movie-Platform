import React from "react";
import Slider from "react-slick";
import { CheckCircle, Clock } from "lucide-react";
import "./Hero.css";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCirclePlay,
  faChevronLeft,
  faChevronRight,
} from "@fortawesome/free-solid-svg-icons";
import { Link, useNavigate } from "react-router-dom";
import { useUser } from "../context/UserContext";
import { usePopup } from "../../hooks/usePopup";

const PrevArrow = ({ onClick }) => (
  <div className="custom-arrow custom-prev-arrow" onClick={onClick}>
    <FontAwesomeIcon icon={faChevronLeft} />
  </div>
);

const NextArrow = ({ onClick }) => (
  <div className="custom-arrow custom-next-arrow" onClick={onClick}>
    <FontAwesomeIcon icon={faChevronRight} />
  </div>
);

const Hero = ({ movies }) => {
  const { user, isWatched, isToWatch, toggleMovieStatus } = useUser();
  const { popup, showPopup } = usePopup();
  const navigate = useNavigate();

  const handleWatchlistClick = async (movieId, listType) => {
    await toggleMovieStatus(movieId, listType, showPopup);
  };

  const settings = {
    dots: true,
    infinite: true,
    speed: 500,
    fade: false,
    slidesToShow: 1,
    slidesToScroll: 1,
    arrows: true,
    autoplay: true,
    autoplaySpeed: 3000,
    pauseOnHover: true,
    adaptiveHeight: false,
    prevArrow: <PrevArrow />,
    nextArrow: <NextArrow />,
  };

  if (!movies || movies.length === 0) {
    return <div>Loading movies...</div>;
  }

  return (
    <div className="movie-carousel-container">
      {popup.show && (
        <div className={`popup-notification ${popup.type}`}>
          {popup.message}
        </div>
      )}

      <Slider {...settings}>
        {movies.map((movie, index) => (
          <div key={movie.imdbId || movie.title || index}>
            <div
              className="movie-card"
              style={{
                backgroundImage: `linear-gradient(
                  to bottom,
                  rgba(0,0,0,0.3),
                  rgba(0,0,0,0.8)
                ), url(${movie.backdrops?.[0] || ""})`,
              }}
            >
              <div className="movie-detail">
                <div className="movie-poster">
                  <img
                    src={movie.poster}
                    alt={movie.title}
                    onError={(e) => {
                      e.target.src =
                        "https://via.placeholder.com/300x450/cccccc/666666?text=No+Image";
                    }}
                  />
                </div>
                <div className="movie-info">
                  <div className="movie-title">
                    <h4>{movie.title}</h4>
                  </div>
                  <div
                    className="movie-buttons-container"
                    style={{ gap: "1rem" }}
                  >
                    {movie.trailerLink && (
                      <Link
                        to={`/Trailer/${movie.trailerLink.substring(
                          movie.trailerLink.length - 11
                        )}`}
                      >
                        <div className="play-button-icon-container">
                          <FontAwesomeIcon
                            className="play-button-icon"
                            icon={faCirclePlay}
                          />
                        </div>
                      </Link>
                    )}
                    <button
                      className="review-button"
                      onClick={() => navigate(`/Reviews/${movie.imdbId}`)}
                    >
                      Reviews
                    </button>
                  </div>

                  <div className="watchlist-buttons-container">
                    <button
                      className={`watchlist-button watched-button ${
                        isWatched(movie.id) ? "active" : ""
                      }`}
                      onClick={(e) => {
                        e.stopPropagation();
                        handleWatchlistClick(movie.id, "watched");
                      }}
                      title="Mark as watched"
                    >
                      <CheckCircle size={20} />
                      <span>Watched</span>
                    </button>

                    <button
                      className={`watchlist-button to-watch-button ${
                        isToWatch(movie.id) ? "active" : ""
                      }`}
                      onClick={(e) => {
                        e.stopPropagation();
                        handleWatchlistClick(movie.id, "toWatch");
                      }}
                      title="Mark as to watch"
                    >
                      <Clock size={20} />
                      <span>To Watch</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        ))}
      </Slider>
    </div>
  );
};

export default Hero;
