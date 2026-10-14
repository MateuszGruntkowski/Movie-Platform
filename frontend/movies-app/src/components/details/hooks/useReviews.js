import { useEffect, useState } from "react";
import { reviewsService } from "../../../services/reviewsService.js";

const REVIEWS_PAGE_SIZE = 10;
const EMPTY_PAGE = { content: [], totalElements: 0, totalPages: 0 };

export function useReviews(movieId, showPopup) {
    const [page, setPage] = useState(0);
    const [data, setData] = useState(EMPTY_PAGE);
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState(null);
    const [reloadKey, setReloadKey] = useState(0);

    useEffect(() => {
        if (!movieId) return;
        let cancelled = false;

        reviewsService
            .getReviewsForMovie(movieId, {
                page,
                size: REVIEWS_PAGE_SIZE,
                sort: "createdAt,desc",
            })
            .then((res) => {
                if (cancelled) return;
                setData({
                    content: res.content ?? [],
                    totalElements: res.totalElements ?? 0,
                    totalPages: res.totalPages ?? 0,
                });
                setError(null);
            })
            .catch((err) => {
                if (cancelled) return;
                console.error("Error fetching reviews:", err);
                setError("Could not load reviews.");
            })
            .finally(() => {
                if (!cancelled) setIsLoading(false);
            });

        return () => {
            cancelled = true;
        };
    }, [movieId, page, reloadKey]);

    const goToPage = (nextPage) => {
        if (nextPage === page || nextPage < 0) return;
        setIsLoading(true);
        setPage(nextPage);
    };

    const reload = () => {
        setIsLoading(true);
        setError(null);
        setReloadKey((k) => k + 1);
    };

    const addReview = async (text) => {
        if (!text.trim() || isSubmitting) return false;
        setIsSubmitting(true);
        try {
            await reviewsService.createReview(movieId, text);
            showPopup?.("Review added!", "success");
            setIsLoading(true);
            setError(null);
            setPage(0);
            setReloadKey((k) => k + 1);
            return true;
        } catch (err) {
            console.error("Error adding review:", err);
            showPopup?.("Could not add review.", "error");
            return false;
        } finally {
            setIsSubmitting(false);
        }
    };

    const deleteReview = async (reviewId) => {
        try {
            await reviewsService.deleteReview(reviewId);
            showPopup?.("Review deleted!", "success");
            setIsLoading(true);
            // If that was the last review on this page, step back one page.
            if (data.content.length === 1 && page > 0) setPage(page - 1);
            setReloadKey((k) => k + 1);
        } catch (err) {
            console.error("Error deleting review:", err);
            showPopup?.("Could not delete review.", "error");
        }
    };

    return {
        reviews: data.content,
        totalReviews: data.totalElements,
        totalPages: data.totalPages,
        page,
        isLoading,
        isSubmitting,
        error,
        goToPage,
        reload,
        addReview,
        deleteReview,
    };
}