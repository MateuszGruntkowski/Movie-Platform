import { Film, Eye, MessageSquare, Star } from "lucide-react";

const FEATURES = [
    { icon: Eye, label: "Track watched movies" },
    { icon: Film, label: "Build your watchlist" },
    { icon: MessageSquare, label: "Write reviews" },
    { icon: Star, label: "Rate your favorites" },
];

const AuthWelcome = () => (
    <div className="auth-welcome">
        <div className="welcome-content">
            <div className="welcome-icon">
                <Film size={48} />
            </div>
            <h1>Welcome to Movie App</h1>
            <p className="welcome-description">
                Create your personal watchlist and track your watched movies, add
                reviews, and discover your next favorite film.
            </p>
            <div className="features-list">
                {FEATURES.map(({ icon: Icon, label }) => (
                    <div className="feature-item" key={label}>
                        <Icon size={20} />
                        <span>{label}</span>
                    </div>
                ))}
            </div>
        </div>
    </div>
);

export default AuthWelcome;