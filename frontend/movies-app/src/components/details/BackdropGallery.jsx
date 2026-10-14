import { useState } from "react";
import "./BackdropGallery.css";

const BackdropGallery = ({ backdrops, title }) => {
    const [active, setActive] = useState(0);
    const [failed, setFailed] = useState(() => new Set());

    const images = (backdrops ?? []).filter((src) => src && !failed.has(src));
    if (images.length === 0) return null;

    const current = images[Math.min(active, images.length - 1)];
    const currentIndex = images.indexOf(current);

    const markFailed = (src) =>
        setFailed((prev) => new Set(prev).add(src));

    return (
        <div className="backdrop-gallery">
            <img
                key={current}
                src={current}
                alt={`${title || "Film"} — still ${currentIndex + 1} of ${images.length}`}
                className="backdrop-gallery-main"
                onError={() => markFailed(current)}
            />

            {images.length > 1 && (
                <div className="backdrop-gallery-thumbs">
                    {images.map((src, i) => (
                        <button
                            key={src}
                            type="button"
                            className={`backdrop-thumb ${i === currentIndex ? "active" : ""}`}
                            onClick={() => setActive(i)}
                            aria-label={`Show still ${i + 1}`}
                            aria-current={i === currentIndex}
                        >
                            <img src={src} alt="" loading="lazy" onError={() => markFailed(src)} />
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
};

export default BackdropGallery;