import "./App.css";
import { useState, useEffect } from "react";
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
import Profile from "./components/profile/Profile";
import { trendingMoviesService } from "./services/trendingMoviesService";

function App() {
  const [trendingMovies, setTrendingMovies] = useState([]);

  useEffect(() => {
    const fetchTrendingMovies = async () => {
      try {
        const data = await trendingMoviesService.getTrending();
        setTrendingMovies(data);
      } catch (error) {
        console.error("Error fetching trending movies:", error);
      }
    };

    fetchTrendingMovies();
  }, []);

  return (
      <div className="App">
        <Header />
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route path="/" element={<Home trendingMovies={trendingMovies} />} />
            <Route path="/Trailer/:ytTrailerId" element={<Trailer />} />
            <Route path="/Details/:movieId" element={<Details />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/search" element={<SearchResults />} />
            <Route path="/watchlist" element={<WatchList />} />
            <Route path="/profile/:username" element={<Profile />} />
            <Route path="*" element={<NotFound />} />
          </Route>
        </Routes>
      </div>
  );
}

export default App;