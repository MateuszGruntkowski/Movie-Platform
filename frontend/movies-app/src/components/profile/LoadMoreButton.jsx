const LoadMoreButton = ({ hasMore, isLoadingMore, onLoadMore }) => {
    if (!hasMore) return null;

    return (
        <button
            type="button"
            className="submit-btn"
            onClick={onLoadMore}
            disabled={isLoadingMore}
        >
            {isLoadingMore ? "Loading..." : "Show more"}
        </button>
    );
};

export default LoadMoreButton;