import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faVideoSlash } from "@fortawesome/free-solid-svg-icons";
import Button from "react-bootstrap/Button";
import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import { NavLink, Link } from "react-router-dom";
import {useAuth} from "../../context/AuthContext";
import MovieSearchBar from "./MovieSearchBar";
import "./Header.css";

const Header = () => {
  const { user, logout } = useAuth();

  return (
      <Navbar variant="dark" expand="lg" className="app-navbar">
        <Container fluid>
          <Navbar.Brand href="/" className="app-brand">
            <FontAwesomeIcon icon={faVideoSlash} />
            <span>Movies App</span>
          </Navbar.Brand>
          <Navbar.Toggle aria-controls="navbarScroll" />
          <Navbar.Collapse id="navbarScroll">
            <Nav
                className="my-2 my-lg-0"
                style={{ maxHeight: "100px" }}
                navbarScroll
            >
              <NavLink className="nav-link app-nav-link" to="/">
                Home
              </NavLink>
              <NavLink className="nav-link app-nav-link" to="/watchList">
                Watch list
              </NavLink>
              {user && (
                  <NavLink className="nav-link app-nav-link" to={`/profile/${user.username}`}>
                    Profile
                  </NavLink>
              )}
            </Nav>

            <div className="header-search-center">
              <MovieSearchBar />
            </div>

            <div className="d-flex ms-auto">
              {user ? (
                  <button className="nav-link app-nav-link nav-link-btn" onClick={logout}>
                    Logout
                  </button>
              ) : (
                  <>
                    <NavLink
                        to="/login"
                        state={{ from: "/" }}
                        className="nav-link app-nav-link me-2"
                    >
                      Login
                    </NavLink>
                    <NavLink to="/register" className="nav-link app-nav-link">
                      Register
                    </NavLink>
                  </>
              )}
            </div>
          </Navbar.Collapse>
        </Container>
      </Navbar>
  );
};

export default Header;