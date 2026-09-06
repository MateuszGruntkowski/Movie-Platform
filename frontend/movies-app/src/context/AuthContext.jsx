import { createContext, useContext, useState, useEffect } from "react";
import { authService } from "../services/authService";
import { userService } from "../services/userService";

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
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
                localStorage.removeItem("token");
                localStorage.removeItem("expiresIn");
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

    return (
        <AuthContext.Provider value={{ user, login, register, logout, loading }}>
            {children}
        </AuthContext.Provider>
    );
};