import { createContext, useContext, useState, useEffect } from "react";
import api from "../../api/axiosConfig";

const UserContext = createContext(null);

export const useUser = () => useContext(UserContext);

export const UserProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      setLoading(false);
      return;
    }

    const fetchUser = async () => {
      try {
        const res = await api.get("/v1/users/me");
        console.log("Fetched user:", res.data);
        setUser(res.data);
      } catch (err) {
        console.error("Błąd pobierania usera:", err);
        setUser(null);
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, []);

  const login = async (username, password) => {
    const res = await api.post("/v1/auth/login", { username, password });
    localStorage.setItem("token", res.data.token);
    localStorage.setItem("expiresIn", res.data.expiresIn);

    const me = await api.get("/v1/users/me");
    console.log("Logged in user:", me.data);
    setUser(me.data);
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("expiresIn");
    setUser(null);
  };

  // Uniwersalna funkcja do zarządzania watchlistą
  const updateWatchlistState = (movieId, listType, action) => {
    const listKey =
      listType === "watched" ? "moviesWatchedIds" : "moviesToWatchIds";

    setUser((prev) => {
      if (!prev) return prev;

      const currentList = prev[listKey] || [];

      if (action === "add") {
        return {
          ...prev,
          [listKey]: currentList.includes(movieId)
            ? currentList
            : [...currentList, movieId],
        };
      } else if (action === "remove") {
        return {
          ...prev,
          [listKey]: currentList.filter((id) => id !== movieId),
        };
      }

      return prev;
    });
  };

  // Uniwersalna funkcja do przełączania statusu filmu
  const toggleMovieStatus = async (movieId, listType, showPopup) => {
    if (!user) {
      showPopup?.("Zaloguj się, aby dodać do listy!", "login");
      return false;
    }

    const listKey =
      listType === "watched" ? "moviesWatchedIds" : "moviesToWatchIds";
    const isInList = user[listKey]?.includes(movieId) ?? false;

    const endpoint = listType === "watched" ? "watched" : "toWatch";
    const otherListType = listType === "watched" ? "toWatch" : "watched";
    const otherListKey =
      listType === "watched" ? "moviesToWatchIds" : "moviesWatchedIds";
    const otherEndpoint = listType === "watched" ? "toWatch" : "watched";
    const isInOtherList = user[otherListKey]?.includes(movieId) ?? false;

    try {
      if (isInList) {
        // Usuwamy z obecnej listy
        await api.delete(`/v1/users/watchlist/${endpoint}/${movieId}`);
        updateWatchlistState(movieId, listType, "remove");
        showPopup?.(`Removed from ${listType}!`, listType);
      } else {
        // Dodajemy do nowej listy
        await api.patch(`/v1/users/watchlist/${endpoint}/${movieId}`);
        updateWatchlistState(movieId, listType, "add");

        // Jeśli film był w drugiej liście, usuwamy go stamtąd
        if (isInOtherList) {
          try {
            await api.delete(`/v1/users/watchlist/${otherEndpoint}/${movieId}`);
            updateWatchlistState(movieId, otherListType, "remove");
          } catch (error) {
            console.error(`Error removing from ${otherListType}:`, error);
          }
        }

        showPopup?.(`Added to ${listType}!`, listType);
      }
      return true;
    } catch (error) {
      console.error(`Error toggling ${listType}:`, error);
      return false;
    }
  };

  // Funkcje pomocnicze do sprawdzania statusu
  const isWatched = (movieId) =>
    user?.moviesWatchedIds?.includes(movieId) ?? false;
  const isToWatch = (movieId) =>
    user?.moviesToWatchIds?.includes(movieId) ?? false;

  // Funkcje do przenoszenia między listami (dla WatchList)
  const moveToWatched = async (movieId) => {
    try {
      await api.patch(`/v1/users/watchlist/watched/${movieId}`);
      updateWatchlistState(movieId, "toWatch", "remove");
      updateWatchlistState(movieId, "watched", "add");
      return true;
    } catch (error) {
      console.error("Error moving to watched:", error);
      return false;
    }
  };

  const moveToWatch = async (movieId) => {
    try {
      await api.patch(`/v1/users/watchlist/toWatch/${movieId}`);
      updateWatchlistState(movieId, "watched", "remove");
      updateWatchlistState(movieId, "toWatch", "add");
      return true;
    } catch (error) {
      console.error("Error moving to watch:", error);
      return false;
    }
  };

  // Funkcje do usuwania z list
  const removeFromList = async (movieId, listType) => {
    const endpoint = listType === "watched" ? "watched" : "toWatch";

    try {
      await api.delete(`/v1/users/watchlist/${endpoint}/${movieId}`);
      updateWatchlistState(movieId, listType, "remove");
      return true;
    } catch (error) {
      console.error(`Error removing from ${listType}:`, error);
      return false;
    }
  };

  return (
    <UserContext.Provider
      value={{
        user,
        login,
        logout,
        loading,
        // Status checking
        isWatched,
        isToWatch,
        // Toggle functions
        toggleMovieStatus,
        // Move between lists
        moveToWatched,
        moveToWatch,
        // Remove from lists
        removeFromList,
        // Legacy functions (for backward compatibility)
        addWatched: (movieId) =>
          updateWatchlistState(movieId, "watched", "add"),
        removeWatched: (movieId) =>
          updateWatchlistState(movieId, "watched", "remove"),
        addToWatch: (movieId) =>
          updateWatchlistState(movieId, "toWatch", "add"),
        removeToWatch: (movieId) =>
          updateWatchlistState(movieId, "toWatch", "remove"),
      }}
    >
      {children}
    </UserContext.Provider>
  );
};
