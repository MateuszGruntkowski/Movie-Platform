import React from "react";

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
    <div className="sr-pagination">
      <button
        className="sr-page-btn"
        onClick={handlePrevious}
        disabled={currentPage === 1}
      >
        Previous
      </button>
      <span className="sr-page-info">
        Page {currentPage} of {totalPages}
      </span>
      <button
        className="sr-page-btn"
        onClick={handleNext}
        disabled={currentPage === totalPages}
      >
        Next
      </button>
    </div>
  );
};

export default SearchResultsPagination;
