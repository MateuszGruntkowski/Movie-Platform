import api from "../api/axiosConfig";

export const authService = {
    async login(username, password) {
        try {
            const response = await api.post("/v1/auth/login", { username, password });
            return response.data;
        } catch (error) {
            console.error("Error logging in:", error);
            throw error;
        }
    },

    async register(username, password) {
        try {
            const response = await api.post("/v1/auth/register", { username, password });
            return response.data;
        } catch (error) {
            console.error("Error registering:", error);
            throw error;
        }
    },
};