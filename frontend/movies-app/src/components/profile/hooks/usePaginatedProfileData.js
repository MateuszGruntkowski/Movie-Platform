import { useEffect, useState } from "react";

export function usePaginatedProfileData(fetchPage, pageSize, defaultSort = "newest") {
    const [items, setItems] = useState([]);
    const [page, setPage] = useState(0);
    const [sort, setSort] = useState(defaultSort);
    const [hasMore, setHasMore] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const [isLoadingMore, setIsLoadingMore] = useState(false);

    const loadFirstPage = async (sortValue) => {
        setIsLoading(true);
        try {
            const data = await fetchPage(0, pageSize, sortValue);
            setItems(data.content);
            setHasMore(!data.last);
            setPage(0);
        } catch (err) {
            console.error("Error fetching data:", err);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        let isCancelled = false;

        (async () => {
            setIsLoading(true);
            try {
                const data = await fetchPage(0, pageSize, defaultSort);
                if (isCancelled) return;
                setItems(data.content);
                setHasMore(!data.last);
                setPage(0);
            } catch (err) {
                if (!isCancelled) console.error("Error fetching data:", err);
            } finally {
                if (!isCancelled) setIsLoading(false);
            }
        })();

        return () => {
            isCancelled = true;
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const changeSort = (newSort) => {
        setSort(newSort);
        loadFirstPage(newSort);
    };

    const loadMore = async () => {
        const nextPage = page + 1;
        setIsLoadingMore(true);
        try {
            const data = await fetchPage(nextPage, pageSize, sort);
            setItems((prev) => [...prev, ...data.content]);
            setPage(nextPage);
            setHasMore(!data.last);
        } catch (err) {
            console.error("Error loading more data:", err);
        } finally {
            setIsLoadingMore(false);
        }
    };

    return { items, setItems, sort, changeSort, hasMore, isLoading, isLoadingMore, loadMore };
}