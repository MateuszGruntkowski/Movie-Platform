import { useEffect, useRef } from "react";
import { X } from "lucide-react";

const TrailerModal = ({ trailer, title, onClose }) => {
    const closeRef = useRef(null);

    useEffect(() => {
        const previouslyFocused = document.activeElement;
        const previousOverflow = document.body.style.overflow;
        document.body.style.overflow = "hidden";
        closeRef.current?.focus();

        const onKey = (e) => {
            if (e.key === "Escape") onClose();
        };
        document.addEventListener("keydown", onKey);

        return () => {
            document.removeEventListener("keydown", onKey);
            document.body.style.overflow = previousOverflow;
            previouslyFocused?.focus?.();
        };
    }, [onClose]);

    return (
        <div
            className="trailer-modal"
            role="dialog"
            aria-modal="true"
            aria-label={`Trailer: ${title}`}
            onClick={onClose}
        >
            <button
                ref={closeRef}
                type="button"
                className="trailer-modal__close"
                onClick={onClose}
                aria-label="Close trailer"
            >
                <X size={22} strokeWidth={1.5} />
            </button>
            <div className="trailer-modal__frame" onClick={(e) => e.stopPropagation()}>
                {trailer.kind === "video" ? (
                    <video src={trailer.src} controls autoPlay playsInline />
                ) : (
                    <iframe
                        src={trailer.src}
                        title={`${title} trailer`}
                        allow="autoplay; encrypted-media; picture-in-picture; fullscreen"
                        allowFullScreen
                    />
                )}
            </div>
        </div>
    );
};

export default TrailerModal;