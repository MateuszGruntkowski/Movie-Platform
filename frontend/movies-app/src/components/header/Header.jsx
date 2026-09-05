import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faVideoSlash } from "@fortawesome/free-solid-svg-icons";
import Button from "react-bootstrap/Button";
import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import { NavLink, Link } from "react-router-dom";
import {useAuth} from "../context/AuthContext";
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
                  <Button variant="outline-warning" onClick={logout}>
                    Logout
                  </Button>
              ) : (
                  <>
                    <Button
                        as={Link}
                        to="/login"
                        variant="outline-warning"
                        className="me-2"
                        state={{ from: "/" }}
                    >
                      Login
                    </Button>
                    <Button as={Link} to="/register" variant="outline-warning">
                      Register
                    </Button>
                  </>
              )}
            </div>
          </Navbar.Collapse>
        </Container>
      </Navbar>
  );
};

export default Header;