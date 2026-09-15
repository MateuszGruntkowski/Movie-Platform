import "./LoadMoreButton.css"

const LoadMoreButton = ({ hasMore, isLoadingMore, onLoadMore }) => {
    if (!hasMore) return null;

    return (
        <button
            type="button"
            className="profile-load-more-btn"
            onClick={onLoadMore}
            disabled={isLoadingMore}
        >
            {isLoadingMore ? "Loading..." : "Show more"}
        </button>
    );
};

export default LoadMoreButton;