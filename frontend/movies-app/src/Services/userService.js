import api from "../api/axiosConfig";

export const userService = {
    async getCurrentUser() {
        try {
            const response = await api.get("/v1/users/me");
            return response.data;
        } catch (error) {
            console.error("Error fetching current user:", error);
            throw error;
        }
    }
};