import "./Popup.css";

const Popup = ({ popup }) => {
    if (!popup.show) return null;

    return (
        <div className={`popup-notification ${popup.type}`}>
            {popup.message}
        </div>
    );
};

export default Popup;