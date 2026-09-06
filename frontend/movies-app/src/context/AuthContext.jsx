import { createContext, useContext, useState, useEffect } from "react";
import { authService } from "../services/authService";
import { userService } from "../services/userService";

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    const storeSession = (data) => {
        const expiresAt = Date.now() + data.expiresIn * 1000;
        localStorage.setItem("token", data.token);
        localStorage.setItem("expiresAt", String(expiresAt));
    };

    const clearSession = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("expiresAt");
        setUser(null);
    };

    useEffect(() => {
        const handleLogout = () => setUser(null);
        window.addEventListener("auth:logout", handleLogout);
        return () => window.removeEventListener("auth:logout", handleLogout);
    }, []);

    useEffect(() => {
        const token = localStorage.getItem("token");
        const expiresAt = localStorage.getItem("expiresAt");

        if (!token || (expiresAt && Date.now() > Number(expiresAt))) {
            clearSession();
            setLoading(false);
            return;
        }

        const fetchUser = async () => {
            try {
                const data = await userService.getCurrentUser();
                setUser(data);
            } catch (err) {
                clearSession();
                throw err;
            } finally {
                setLoading(false);
            }
        };

        fetchUser();
    }, []);

    const login = async (username, password) => {
        const data = await authService.login(username, password);
        storeSession(data);

        try {
            const me = await userService.getCurrentUser();
            setUser(me);
        } catch (err) {
            clearSession();
            throw err;
        }
    };

    const register = async (username, password) => {
        const data = await authService.register(username, password);
        storeSession(data);

        try {
            const me = await userService.getCurrentUser();
            setUser(me);
        } catch (err) {
            clearSession();
            throw err;
        }
    };

    const logout = () => {
        clearSession();
    };

    return (
        <AuthContext.Provider value={{ user, login, register, logout, loading }}>
            {children}
        </AuthContext.Provider>
    );
};