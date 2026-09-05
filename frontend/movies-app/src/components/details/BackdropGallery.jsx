import React, { useState } from "react";
import "./BackdropGallery.css";

const BackdropGallery = ({ backdrops, title }) => {
    const [active, setActive] = useState(1);

    if (!backdrops?.length) return null;

    return (
        <div className="backdrop-gallery">
            <img
                src={backdrops[active]}
                alt={`${title || "Film"} — kadr ${active + 1}`}
                className="backdrop-gallery-main"
            />
            <div className="backdrop-gallery-thumbs">
                {backdrops.map((src, i) => (
                    <button
                        key={src}
                        type="button"
                        className={`backdrop-thumb ${i === active ? "active" : ""}`}
                        onClick={() => setActive(i)}
                        aria-label={`Pokaż kadr ${i + 1}`}
                    >
                        <img src={src} alt="" />
                    </button>
                ))}
            </div>
        </div>
    );
};

export default BackdropGallery;