import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useLocation } from "react-router-dom";
import { useUser } from "../../context/UserContext";
import { Film, Eye, MessageSquare, Star } from "lucide-react";
import "../Auth.css";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const { login } = useUser();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      await login(username, password);
      navigate(location.state?.from || "/");
    } catch (error) {
      alert("Login failed: " + error.message);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-wrapper">
        {/* Welcome Section */}
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
              <div className="feature-item">
                <Eye size={20} />
                <span>Track watched movies</span>
              </div>
              <div className="feature-item">
                <Film size={20} />
                <span>Build your watchlist</span>
              </div>
              <div className="feature-item">
                <MessageSquare size={20} />
                <span>Write reviews</span>
              </div>
              <div className="feature-item">
                <Star size={20} />
                <span>Rate your favorites</span>
              </div>
            </div>
          </div>
        </div>

        {/* Login Form */}
        <form className="auth-form" onSubmit={handleLogin}>
          <h2>Login</h2>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <button type="submit">Log in</button>
          <p className="switch-link">
            Don't have an account? <Link to="/register">Register</Link>
          </p>
        </form>
      </div>
    </div>
  );
};

export default Login;
