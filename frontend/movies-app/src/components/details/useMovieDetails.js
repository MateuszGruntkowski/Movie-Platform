import { useEffect, useState } from "react";
import { movieDetailsService } from "../../services/movieDetailsService";

export function useMovieDetails(movieId) {
    const [movie, setMovie] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!movieId) return;
        let cancelled = false;

        setIsLoading(true);
        setError(null);
        setMovie(null);

        movieDetailsService
            .getMovieDetails(movieId)
            .then((data) => {
                if (cancelled) return;
                setMovie(data);
            })
            .catch((err) => {
                if (cancelled) return;
                console.error("Error fetching movie details:", err);
                setError("Failed to load movie details.");
            })
            .finally(() => {
                if (!cancelled) setIsLoading(false);
            });

        return () => {
            cancelled = true;
        };
    }, [movieId]);

    return { movie, isLoading, error };
}