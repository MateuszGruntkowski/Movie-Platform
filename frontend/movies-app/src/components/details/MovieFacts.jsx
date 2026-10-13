import { formatCurrency, formatLanguage } from "../../utils/movieUtils.js";
import "./MovieFacts.css";

const MovieFacts = ({ movie }) => {
    const facts = [
        { label: "Original language", value: formatLanguage(movie.originalLanguage) },
        { label: "Budget", value: formatCurrency(movie.budget) },
        { label: "Revenue", value: formatCurrency(movie.revenue) },
        {
            label: "Adult content",
            value: typeof movie.adult === "boolean" ? (movie.adult ? "Yes" : "No") : null,
        },
    ].filter((fact) => fact.value);

    if (facts.length === 0) return null;

    return (
        <dl className="details-facts">
            {facts.map(({ label, value }) => (
                <div key={label} className="details-fact">
                    <dt>{label}</dt>
                    <dd>{value}</dd>
                </div>
            ))}
        </dl>
    );
};

export default MovieFacts;