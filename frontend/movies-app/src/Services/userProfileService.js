import api from "../api/axiosConfig";

export const userProfileService = {
    async getMyProfile() {
        try {
            const response = await api.get("/v1/users/me/profile");
            return response.data;
        } catch (error) {
            console.error("Error fetching user profile:", error);
            throw error;
        }
    },
};