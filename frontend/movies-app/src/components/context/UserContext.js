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

  const addWatched = (movieId) => {
    setUser((prev) =>
      prev
        ? { ...prev, moviesWatchedIds: [...prev.moviesWatchedIds, movieId] }
        : prev
    );
  };

  const removeWatched = (movieId) => {
    setUser((prev) =>
      prev
        ? {
            ...prev,
            moviesWatchedIds: prev.moviesWatchedIds.filter(
              (id) => id !== movieId
            ),
          }
        : prev
    );
  };

  const addToWatch = (movieId) => {
    setUser((prev) =>
      prev
        ? { ...prev, moviesToWatchIds: [...prev.moviesToWatchIds, movieId] }
        : prev
    );
  };

  const removeToWatch = (movieId) => {
    setUser((prev) =>
      prev
        ? {
            ...prev,
            moviesToWatchIds: prev.moviesToWatchIds.filter(
              (id) => id !== movieId
            ),
          }
        : prev
    );
  };

  return (
    <UserContext.Provider
      value={{
        user,
        login,
        logout,
        loading,
        addToWatch,
        removeToWatch,
        addWatched,
        removeWatched,
      }}
    >
      {children}
    </UserContext.Provider>
  );
};
