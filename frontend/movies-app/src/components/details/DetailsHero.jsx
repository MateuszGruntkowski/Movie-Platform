import "./DetailsHero.css";

const DetailsHero = ({ movie }) => {
    if (movie?.backdropPath) {
        return (
            <div
                className="details-hero"
                style={{ backgroundImage: `url(${movie.backdropPath})` }}
            >
                <div className="details-hero-overlay">
                    <h1 className="reviews-title">{movie.title || "Szczegóły filmu"}</h1>
                </div>
            </div>
        );
    }

    return (
        <div className="reviews-header">
            <h1 className="reviews-title">Reviews of the film</h1>
        </div>
    );
};

export default DetailsHero;