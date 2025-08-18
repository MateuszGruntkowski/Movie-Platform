import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { Film, Eye, MessageSquare, Star } from "lucide-react";
import "../Auth.css";

const Register = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch(
        "http://localhost:8080/api/v1/auth/register",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ username, password }),
        }
      );

      if (!response.ok) throw new Error("Registration failed");

      const data = await response.json();
      localStorage.setItem("token", data.token);
      localStorage.setItem("expiresIn", data.expiresIn);

      navigate("/");
    } catch (error) {
      alert(error.message);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-wrapper">
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

        <form className="auth-form" onSubmit={handleRegister}>
          <h2>Register</h2>
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
          <button type="submit">Register</button>
          <p className="switch-link">
            Already have an account? <br />
            <Link to="/login">Log in</Link>
          </p>
        </form>
      </div>
    </div>
  );
};

export default Register;
