import "./Popup.css";

const Popup = ({ popup }) => {
    if (!popup.show) return null;

    return (
        <div className={`popup-notification ${popup.type}`} role="status" aria-live="polite">
            {popup.message}
        </div>
    );
};

export default Popup;