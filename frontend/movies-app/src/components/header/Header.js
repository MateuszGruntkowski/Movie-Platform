import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faVideoSlash } from "@fortawesome/free-solid-svg-icons";
import Button from "react-bootstrap/Button";
import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import { NavLink, Link } from "react-router-dom";
import { useUser } from "../context/UserContext";
import MovieSearchBar from "./MovieSearchBar";

const Header = () => {
  const { user, logout } = useUser();

  return (
    <Navbar bg="dark" variant="dark" expand="lg">
      <Container fluid>
        <Navbar.Brand href="/" style={{ color: "gold" }}>
          <FontAwesomeIcon icon={faVideoSlash} />
          Movies App
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="navbarScroll" />
        <Navbar.Collapse id="navbarScroll">
          <Nav
            className="my-2 my-lg-0"
            style={{ maxHeight: "100px" }}
            navbarScroll
          >
            <NavLink className="nav-link" to="/">
              Home
            </NavLink>
            <NavLink className="nav-link" to="/watchList">
              Watch list
            </NavLink>
          </Nav>

          {/* Search Bar - wyśrodkowany */}
          <div className="mx-auto">
            <MovieSearchBar />
          </div>

          <div className="d-flex">
            {user ? (
              <Button variant="outline-info" onClick={logout}>
                Logout
              </Button>
            ) : (
              <>
                <Button
                  as={Link}
                  to="/login"
                  variant="outline-info"
                  className="me-2"
                  state={{ from: "/" }}
                >
                  Login
                </Button>
                <Button as={Link} to="/register" variant="outline-info">
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
