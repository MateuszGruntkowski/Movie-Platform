import { useEffect, useRef } from "react";
import { X } from "lucide-react";
import "./TrailerModal.css";

const TrailerModal = ({ source, title, onClose }) => {
    const dialogRef = useRef(null);

    useEffect(() => {
        const dialog = dialogRef.current;
        if (dialog && !dialog.open) dialog.showModal();
    }, []);

    const label = `${title || "Movie"} trailer`;

    return (
        <dialog
            ref={dialogRef}
            className="trailer-dialog"
            aria-label={label}
            onClose={onClose}
            onClick={(e) => {
                // A click on the dialog element itself is a click on the backdrop.
                if (e.target === dialogRef.current) onClose();
            }}
        >
            <button
                type="button"
                className="trailer-dialog__close"
                onClick={onClose}
                aria-label="Close trailer"
            >
                <X size={22} />
            </button>

            <div className="trailer-dialog__frame">
                {source.kind === "iframe" ? (
                    <iframe
                        src={source.src}
                        title={label}
                        allow="autoplay; encrypted-media; picture-in-picture; fullscreen"
                        allowFullScreen
                        referrerPolicy="strict-origin-when-cross-origin"
                    />
                ) : (
                    <video src={source.src} controls autoPlay playsInline />
                )}
            </div>
        </dialog>
    );
};

export default TrailerModal;