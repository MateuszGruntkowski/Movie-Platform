import LoadMoreButton from "./LoadMoreButton";

const ProfileEntryList = ({
                              items,
                              isLoading,
                              loadingText,
                              emptyText,
                              hasMore,
                              isLoadingMore,
                              onLoadMore,
                              renderItem,
                          }) => {
    if (isLoading && (!items || items.length === 0)) {
        return <p className="profile-loading">{loadingText}</p>;
    }

    if (!items || items.length === 0) {
        return (
            <div className="profile-empty">
                <p>{emptyText}</p>
            </div>
        );
    }

    return (
        <div className="profile-list">
            {items.map(renderItem)}
            <LoadMoreButton
                hasMore={hasMore}
                isLoadingMore={isLoadingMore}
                onLoadMore={onLoadMore}
            />
        </div>
    );
};

export default ProfileEntryList;