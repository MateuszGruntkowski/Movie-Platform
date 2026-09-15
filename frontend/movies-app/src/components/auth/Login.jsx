import { useState } from "react";
import { useNavigate, Link, useLocation } from "react-router-dom";
import { useAuth } from "../../context/AuthContext.jsx";
import AuthLayout from "./AuthLayout";
import "./Auth.css";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const { login } = useAuth();
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
      <AuthLayout>
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
      </AuthLayout>
  );
};

export default Login;