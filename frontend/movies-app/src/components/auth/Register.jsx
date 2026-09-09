import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../../context/AuthContext.jsx";
import AuthLayout from "./AuthLayout";
import "./Auth.css";

const Register = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      await register(username, password);
      navigate("/");
    } catch (error) {
      alert("Registration failed: " + error.message);
    }
  };

  return (
      <AuthLayout>
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
      </AuthLayout>
  );
};

export default Register;