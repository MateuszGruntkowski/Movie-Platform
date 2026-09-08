import { useState, useCallback, useEffect } from "react";

const PAGE_SIZE = 10;

const initialState = {
    movies: [],
    total: 0,
    page: 0,
    isLast: true,
    isLoading: false,
};

export const useWatchlistList = (fetcher, enabled) => {
    const [state, setState] = useState(initialState);

    const loadPage = useCallback(async (page) => {
        setState((prev) => ({ ...prev, isLoading: true }));
        try {
            const data = await fetcher({ page, size: PAGE_SIZE });
            setState((prev) => ({
                movies: page === 0 ? data.content : [...prev.movies, ...data.content],
                total: data.totalElements,
                page,
                isLast: data.last,
                isLoading: false,
            }));
        } catch (error) {
            console.error("Error loading watchlist page:", error);
            setState((prev) => ({ ...prev, isLoading: false }));
        }
    }, [fetcher]);

    useEffect(() => {
        if (enabled) {
            loadPage(0);
        }
    }, [enabled, loadPage]);

    const loadMore = () => {
        if (!state.isLast && !state.isLoading) {
            loadPage(state.page + 1);
        }
    };

    const addMovie = useCallback((movie) => {
        setState((prev) => ({
            ...prev,
            movies: [movie, ...prev.movies],
            total: prev.total + 1,
        }));
    }, []);

    const removeMovie = useCallback((tmdbId) => {
        setState((prev) => ({
            ...prev,
            movies: prev.movies.filter((m) => m.tmdbId !== tmdbId),
            total: Math.max(0, prev.total - 1),
        }));
    }, []);

    return { state, loadMore, addMovie, removeMovie };
};