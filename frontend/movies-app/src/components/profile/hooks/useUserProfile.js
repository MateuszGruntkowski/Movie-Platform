import { useEffect, useState } from "react";
import { userProfileService } from "../../../services/userProfileService";

export function useUserProfile(username) {
    const [profile, setProfile] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isUnauthorized, setIsUnauthorized] = useState(false);

    useEffect(() => {
        if (!username) return;
        let isCancelled = false;

        userProfileService
            .getUserProfile(username)
            .then((data) => {
                if (!isCancelled) setProfile(data);
            })
            .catch((err) => {
                if (isCancelled) return;
                console.error("Error fetching profile:", err);
                if (err.response?.status === 401) {
                    setIsUnauthorized(true);
                } else {
                    setError("Failed to load profile. Please try again later.");
                }
            })
            .finally(() => {
                if (!isCancelled) setIsLoading(false);
            });

        return () => {
            isCancelled = true;
        };
    }, [username]);

    return { profile, setProfile, isLoading, error, isUnauthorized };
}