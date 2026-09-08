const WatchlistPopup = ({ popup }) => {
    if (!popup.show) return null;

    return (
        <div className={`popup-notification ${popup.type}`}>
            {popup.message}
        </div>
    );
};

export default WatchlistPopup;