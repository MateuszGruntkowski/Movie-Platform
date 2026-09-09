import { useEffect, useState } from "react";
import { reviewsService } from "../../../services/reviewsService.js";

const REVIEWS_PAGE_SIZE = 10;

export function useReviews(movieId, showPopup) {
    const [reviews, setReviews] = useState([]);
    const [page, setPage] = useState(0);
    const [totalReviews, setTotalReviews] = useState(0);
    const [hasMore, setHasMore] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [isLoadingMore, setIsLoadingMore] = useState(false);

    const fetchPage = (p) =>
        reviewsService.getReviewsForMovie(movieId, {
            page: p,
            size: REVIEWS_PAGE_SIZE,
            sort: "createdAt,desc",
        });

    useEffect(() => {
        if (!movieId) return;
        let cancelled = false;

        setIsLoading(true);
        setPage(0);
        setReviews([]);

        fetchPage(0)
            .then((data) => {
                if (cancelled) return;
                setReviews(data.content);
                setTotalReviews(data.totalElements);
                setHasMore(!data.last);
            })
            .catch((err) => {
                if (cancelled) return;
                console.error("Error fetching reviews:", err);
                showPopup?.("Failed to load review.", "error");
            })
            .finally(() => {
                if (!cancelled) setIsLoading(false);
            });

        return () => {
            cancelled = true;
        };
    }, [movieId]);

    const loadMore = async () => {
        const nextPage = page + 1;
        setIsLoadingMore(true);
        try {
            const data = await fetchPage(nextPage);
            setReviews((prev) => [...prev, ...data.content]);
            setPage(nextPage);
            setHasMore(!data.last);
        } catch (err) {
            console.error("Error loading more reviews:", err);
            showPopup?.("Could not load more reviews.", "error");
        } finally {
            setIsLoadingMore(false);
        }
    };

    const addReview = async (text) => {
        if (!text.trim()) return;
        try {
            const newReview = await reviewsService.createReview(movieId, text);
            setReviews((prev) => [newReview, ...prev]);
            setTotalReviews((prev) => prev + 1);
            showPopup?.("Review added!", "success");
        } catch (err) {
            console.error("Error adding review:", err);
            showPopup?.("Could not add review.", "error");
        }
    };

    const deleteReview = async (reviewId) => {
        try {
            await reviewsService.deleteReview(reviewId);
            setReviews((prev) => prev.filter((r) => r.id !== reviewId));
            setTotalReviews((prev) => Math.max(prev - 1, 0));
            showPopup?.("Review deleted!", "success");
        } catch (err) {
            console.error("Error deleting review:", err);
            showPopup?.("Could not delete review.", "error");
        }
    };

    return {
        reviews,
        totalReviews,
        hasMore,
        isLoading,
        isLoadingMore,
        loadMore,
        addReview,
        deleteReview,
    };
}