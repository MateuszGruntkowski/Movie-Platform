import { ChevronLeft, ChevronRight } from "lucide-react";

// `page` is zero-based, matching Spring's Pageable.
const ReviewsPagination = ({ page, totalPages, onPageChange, disabled = false }) => {
    if (totalPages <= 1) return null;

    return (
        <nav className="reviews-pagination" aria-label="Reviews pages">
            <button
                type="button"
                className="reviews-page-btn"
                onClick={() => onPageChange(page - 1)}
                disabled={disabled || page === 0}
                aria-label="Previous page"
            >
                <ChevronLeft size={18} />
            </button>
            <span className="reviews-page-status" aria-live="polite">
                Page {page + 1} of {totalPages}
            </span>
            <button
                type="button"
                className="reviews-page-btn"
                onClick={() => onPageChange(page + 1)}
                disabled={disabled || page >= totalPages - 1}
                aria-label="Next page"
            >
                <ChevronRight size={18} />
            </button>
        </nav>
    );
};

export default ReviewsPagination;