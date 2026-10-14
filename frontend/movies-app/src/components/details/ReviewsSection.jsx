import { useRef } from "react";
import { useAuth } from "../../context/AuthContext";
import { useReviews } from "./hooks/useReviews.js";
import ReviewForm from "./ReviewForm";
import ReviewList from "./ReviewList";

const ReviewsSection = ({ movieId, showPopup }) => {
    const { user } = useAuth();
    const revText = useRef(null);
    const sectionRef = useRef(null);

    const {
        reviews,
        totalReviews,
        totalPages,
        page,
        isLoading,
        isSubmitting,
        error,
        goToPage,
        reload,
        addReview,
        deleteReview,
    } = useReviews(movieId, showPopup);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const text = revText.current?.value ?? "";
        const ok = await addReview(text);
        if (ok && revText.current) revText.current.value = "";
    };

    const handlePageChange = (nextPage) => {
        goToPage(nextPage);
        sectionRef.current?.scrollIntoView({ behavior: "smooth", block: "start" });
    };

    return (
        <section className="details-section details-reviews" ref={sectionRef}>
            <ReviewForm
                handleSubmit={handleSubmit}
                revText={revText}
                labelText="Write a review"
                isSubmitting={isSubmitting}
            />

            <ReviewList
                reviews={reviews}
                totalReviews={totalReviews}
                page={page}
                totalPages={totalPages}
                isLoading={isLoading}
                error={error}
                onRetry={reload}
                onPageChange={handlePageChange}
                currentUsername={user?.username}
                onDelete={deleteReview}
            />
        </section>
    );
};

export default ReviewsSection;