import Slider from "react-slick";
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
import { useWatchlist } from "../../context/WatchlistContext";
import { usePopup } from "../../hooks/usePopup";
import ToWatchButton from "../buttons/ToWatchButton";
import WatchedButton from "../buttons/WatchedButton";
import Popup from "../common/Popup.jsx";

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

const Hero = ({ trendingMovies }) => {
  const { isWatched, isToWatch, toggleMovieStatus } = useWatchlist();
  const { popup, showPopup } = usePopup();
  const navigate = useNavigate();

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

  if (!trendingMovies || trendingMovies.length === 0) {
    return <div>Loading movies...</div>;
  }

  return (
      <div className="movie-carousel-container">
        <Popup popup={popup} />

        <Slider {...settings}>
          {trendingMovies.map((movie, index) => (
              <div key={movie.tmdbId || movie.imdbId || movie.title || index}>
                <div
                    className="movie-card"
                    style={{
                      backgroundImage: `linear-gradient(
                  to bottom,
                  rgba(0,0,0,0.3),
                  rgba(0,0,0,0.8)
                ), url(${movie.backdrops?.[0] || movie.backdropPath || ""})`,
                    }}
                >
                  <div className="movie-detail">
                    <div className="movie-poster">
                      <img
                          src={movie.posterPath}
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
                        <button
                            className="see-more-button"
                            onClick={() => navigate(`/Details/${movie.tmdbId}`)}
                        >
                          See more
                        </button>
                      </div>
                      <div className="watchlist-buttons-container">
                        <WatchedButton
                            movie={movie}
                            handleWatchlistClick={handleWatchlistClick}
                            isWatched={isWatched}
                        />
                        <ToWatchButton
                            movie={movie}
                            handleWatchlistClick={handleWatchlistClick}
                            isToWatch={isToWatch}
                        />
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