import { useParams } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import ProfileContent from "./ProfileContent";
import "./Profile.css";

const Profile = () => {
    const { username } = useParams();
    const { user } = useAuth();
    const isOwnProfile = user?.username === username;

    return <ProfileContent key={username} username={username} isOwnProfile={isOwnProfile} />;
};

export default Profile;