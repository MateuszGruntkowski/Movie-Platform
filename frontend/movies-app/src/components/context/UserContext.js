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

  // Główna funkcja do toggle - używa tylko jednego endpointa
  const toggleMovieStatus = async (movieId, listType, showPopup) => {
    if (!user) {
      showPopup?.("Zaloguj się, aby dodać do listy!", "login");
      return;
    }

    try {
      await api.put(`/v1/users/watchlist/toggle/${movieId}`, null, {
        params: { listType },
      });

      // Odświeżamy dane użytkownika
      const res = await api.get("/v1/users/me");
      setUser(res.data);

      // Określamy co się stało i pokazujemy odpowiedni komunikat
      const isNowInList =
        listType === "watched"
          ? res.data.moviesWatchedIds?.includes(movieId)
          : res.data.moviesToWatchIds?.includes(movieId);

      const action = isNowInList ? "added" : "removed";
      const listName = listType === "watched" ? "watched" : "watch list";

      showPopup?.(
        `Movie ${action} ${isNowInList ? "to" : "from"} ${listName}!`,
        listType
      );
    } catch (error) {
      console.error(`Error toggling movie status:`, error);
      showPopup?.("Something went wrong!", "error");
    }
  };

  // Proste funkcje pomocnicze
  const isWatched = (movieId) =>
    user?.moviesWatchedIds?.includes(movieId) ?? false;
  const isToWatch = (movieId) =>
    user?.moviesToWatchIds?.includes(movieId) ?? false;

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
      }}
    >
      {children}
    </UserContext.Provider>
  );
};
