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

    async updateAvatar(avatarPath) {
        try {
            const response = await api.put("/v1/users/me/avatar", { avatarPath });
            return response.data;
        } catch (error) {
            console.error("Error updating avatar:", error);
            throw error;
        }
    },
};