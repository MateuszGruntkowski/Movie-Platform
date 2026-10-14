import { useParams } from "react-router-dom";
import Popup from "../common/Popup.jsx";
import { usePopup } from "../../hooks/usePopup";
import { useMovieDetails } from "./hooks/useMovieDetails.js";
import MovieHero from "./MovieHero";
import MovieRating from "./MovieRating";
import BackdropGallery from "./BackdropGallery";
import ReviewsSection from "./ReviewsSection";
import "./Details.css";

const Details = () => {
  const { movieId } = useParams();
  const { popup, showPopup } = usePopup();
  const { movie, isLoading, error } = useMovieDetails(movieId);

  if (error) {
    return (
        <div className="details-page">
          <p className="details-error">{error}</p>
        </div>
    );
  }

  // The first backdrop is used as the page backdrop, so the gallery shows the rest.
  const galleryImages = movie?.backdrops?.slice(1) ?? [];

  return (
      <div className="details-page">
        <Popup popup={popup} />

        <MovieHero movie={movie} isLoading={isLoading} showPopup={showPopup} />

        <MovieRating movieId={movieId} showPopup={showPopup} />

        {galleryImages.length > 0 && (
            <section className="details-section">
              <h2 className="details-section-title">Gallery</h2>
              <BackdropGallery backdrops={galleryImages} title={movie.title} />
            </section>
        )}

        <ReviewsSection key={movieId} movieId={movieId} showPopup={showPopup} />
      </div>
  );
};

export default Details;