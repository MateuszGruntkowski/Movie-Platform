import { useState, useEffect } from "react";
import { movieSearchService } from "../../Services/movieSearchService";

export function useMovieSearch(query, { debounceMs = 300, minLength = 2 } = {}) {
    const [results, setResults] = useState([]);
    const [isSearching, setIsSearching] = useState(false);
    const [showResults, setShowResults] = useState(false);

    useEffect(() => {
        if (query.trim().length < minLength) {
            setResults([]);
            setShowResults(false);
            return;
        }

        const timeoutId = setTimeout(async () => {
            setIsSearching(true);
            try {
                const data = await movieSearchService.searchMovies(query);
                setResults(data?.results ?? []);
                setShowResults(true);
            } catch (error) {
                console.error("Błąd wyszukiwania:", error);
                setResults([]);
                setShowResults(false);
            } finally {
                setIsSearching(false);
            }
        }, debounceMs);

        return () => clearTimeout(timeoutId);
    }, [query, debounceMs, minLength]);

    return { results, isSearching, showResults, setShowResults };
}