import { createContext, useContext, useState, useEffect } from "react";
import api from "../../api/axiosConfig";

const UserContext = createContext(null);

export const useUser = () => useContext(UserContext);

export const UserProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // 🔹 Sprawdź usera przy starcie (jeśli jest token)
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

  // 🔹 Logowanie
  const login = async (username, password) => {
    const res = await api.post("/v1/auth/login", { username, password });
    localStorage.setItem("token", res.data.token);
    localStorage.setItem("expiresIn", res.data.expiresIn);

    // od razu pobierz profil
    const me = await api.get("/v1/users/me");
    console.log("Logged in user:", me.data);
    setUser(me.data);
  };

  // 🔹 Wylogowanie
  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("expiresIn");
    setUser(null);
  };

  return (
    <UserContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </UserContext.Provider>
  );
};
