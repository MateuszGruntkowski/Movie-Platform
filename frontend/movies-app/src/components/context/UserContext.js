import { createContext, useContext, useState, useEffect } from "react";
import { authService } from "../../Services/authService";
import { userService } from "../../Services/userService";

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
        const data = await userService.getCurrentUser();
        setUser(data);
      } catch (err) {
        console.error("Error fetching user:", err);
        setUser(null);
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, []);

  const login = async (username, password) => {
    const data = await authService.login(username, password);
    localStorage.setItem("token", data.token);
    localStorage.setItem("expiresIn", data.expiresIn);

    const me = await userService.getCurrentUser();
    setUser(me);
  };

  const register = async (username, password) => {
    const data = await authService.register(username, password);
    localStorage.setItem("token", data.token);
    localStorage.setItem("expiresIn", data.expiresIn);

    const me = await userService.getCurrentUser();
    setUser(me);
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("expiresIn");
    setUser(null);
  };

  const toggleMovieStatus = async (movieId, listType, showPopup) => {
    if (!user) {
      showPopup?.("Log in to add to the watchlist!", "login");
      return;
    }

    try {
      await userService.toggleWatchlistStatus(movieId, listType);
      const data = await userService.getCurrentUser();
      setUser(data);

      const isNowInList =
          listType === "watched"
              ? data.moviesWatchedIds?.includes(movieId)
              : data.moviesToWatchIds?.includes(movieId);

      const action = isNowInList ? "added" : "removed";
      const listName = listType === "watched" ? "watched" : "watch list";

      showPopup?.(
          `Movie ${action} ${isNowInList ? "to" : "from"} ${listName}!`,
          listType
      );
    } catch (error) {
      showPopup?.("Something went wrong!", "error");
    }
  };

  const isWatched = (movieId) =>
      user?.moviesWatchedIds?.includes(movieId) ?? false;
  const isToWatch = (movieId) =>
      user?.moviesToWatchIds?.includes(movieId) ?? false;

  return (
      <UserContext.Provider
          value={{ user, login, register, logout, loading, isWatched, isToWatch, toggleMovieStatus }}
      >
        {children}
      </UserContext.Provider>
  );
};