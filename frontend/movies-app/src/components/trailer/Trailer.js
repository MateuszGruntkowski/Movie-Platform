import { useParams } from "react-router-dom";
import ReactPlayer from "react-player";
import { useState } from "react";
import "./Trailer.css";

import React from "react";

const Trailer = () => {
  const params = useParams();
  const key = params.ytTrailerId;

  return (
    <iframe
      className="react-player-container"
      width="100%"
      height="400px"
      src={`https://www.youtube.com/embed/${key}?autoplay=0&controls=1`}
      title="YouTube video player"
      frameBorder="0"
      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
      allowFullScreen
    />
  );
};

export default Trailer;
