import { ChevronLeft, ChevronRight } from "lucide-react";
import "./SearchResultsPagination.css";

const SearchResultsPagination = ({ currentPage, totalPages, onPageChange }) => {
    if (totalPages <= 1) return null;

    const handlePrevious = () => {
        if (currentPage > 1) {
            onPageChange(currentPage - 1);
        }
    };

    const handleNext = () => {
        if (currentPage < totalPages) {
            onPageChange(currentPage + 1);
        }
    };

    return (
        <nav className="sr-pagination" aria-label="Search results pages">
            <button
                type="button"
                className="sr-page-btn"
                onClick={handlePrevious}
                disabled={currentPage === 1}
                aria-label="Previous page"
            >
                <ChevronLeft size={18} />
            </button>
            <span className="sr-page-info" aria-live="polite">
        Page {currentPage} of {totalPages}
      </span>
            <button
                type="button"
                className="sr-page-btn"
                onClick={handleNext}
                disabled={currentPage === totalPages}
                aria-label="Next page"
            >
                <ChevronRight size={18} />
            </button>
        </nav>
    );
};

export default SearchResultsPagination;