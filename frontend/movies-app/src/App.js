import "./App.css";
import api from "./api/axiosConfig";
import { useState, useEffect, use } from "react";
import Layout from "./components/Layout";
import { Routes, Route } from "react-router-dom";
import Home from "./components/home/Home";
import Header from "./components/header/Header";
import Trailer from "./components/trailer/Trailer";
import Details from "./components/details/Details";
import NotFound from "./components/notFound/NotFound";
import Login from "./components/auth/login/Login";
import Register from "./components/auth/register/Register";
import WatchList from "./components/watchList/WatchList";
import SearchResults from "./components/searchResults/SearchResults";

function App() {
  const [movies, setMovies] = useState([]);
  const [movie, setMovie] = useState();
  const [reviews, setReviews] = useState();

  const getMovies = async () => {
    try {
      const response = await api.get("v1/movies");
      setMovies(response.data);
      console.log("Movies fetched:", response.data);
    } catch (error) {
      console.error("Error fetching movies:", error);
    }
  };

  useEffect(() => {
    getMovies();
  }, []);

  return (
    <div className="App">
      <Header />
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route path="/" element={<Home movies={movies} />} />
          <Route path="/Trailer/:ytTrailerId" element={<Trailer />} />
          <Route
            path="/Details/:movieId"
            element={
              <Details
                reviews={reviews}
                setReviews={setReviews}
                movie={movie}
                setMovie={setMovie}
              />
            }
          />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/search" element={<SearchResults />} />
          <Route path="/watchlist" element={<WatchList />} />
          <Route path="*" element={<NotFound />} />
        </Route>
      </Routes>
    </div>
  );
}

export default App;
