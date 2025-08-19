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
    setUser(me.data);
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("expiresIn");
    setUser(null);
  };

  // Jedna główna funkcja do zarządzania watchlistą
  const updateWatchlist = async (movieId, action, showPopup) => {
    if (!user) {
      showPopup?.("Zaloguj się, aby dodać do listy!", "login");
      return false;
    }

    try {
      let endpoint;
      let message;

      switch (action) {
        case "add-watched":
          endpoint = `watched/${movieId}`;
          message = "Added to watched!";
          break;
        case "add-toWatch":
          endpoint = `toWatch/${movieId}`;
          message = "Added to watch list!";
          break;
        case "remove-watched":
          endpoint = `watched/${movieId}`;
          message = "Removed from watched!";
          break;
        case "remove-toWatch":
          endpoint = `toWatch/${movieId}`;
          message = "Removed from watch list!";
          break;
        default:
          return false;
      }

      if (action.startsWith("remove")) {
        await api.delete(`/v1/users/watchlist/${endpoint}`);
      } else {
        await api.patch(`/v1/users/watchlist/${endpoint}`);
      }

      // Odświeżamy dane użytkownika
      const res = await api.get("/v1/users/me");
      setUser(res.data);

      showPopup?.(message, action.includes("watched") ? "watched" : "toWatch");
      return true;
    } catch (error) {
      console.error(`Error with watchlist action ${action}:`, error);
      return false;
    }
  };

  // Proste funkcje pomocnicze
  const isWatched = (movieId) =>
    user?.moviesWatchedIds?.includes(movieId) ?? false;
  const isToWatch = (movieId) =>
    user?.moviesToWatchIds?.includes(movieId) ?? false;

  // Funkcja toggle dla Hero komponentu
  const toggleMovieStatus = async (movieId, listType, showPopup) => {
    const isCurrentlyInList =
      listType === "watched" ? isWatched(movieId) : isToWatch(movieId);
    const action = isCurrentlyInList ? `remove-${listType}` : `add-${listType}`;

    return await updateWatchlist(movieId, action, showPopup);
  };

  return (
    <UserContext.Provider
      value={{
        user,
        login,
        logout,
        loading,
        isWatched,
        isToWatch,
        toggleMovieStatus,
        updateWatchlist,
      }}
    >
      {children}
    </UserContext.Provider>
  );
};
