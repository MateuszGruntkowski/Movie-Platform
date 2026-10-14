import { Spinner } from "react-bootstrap";
import "./LoadingSpinner.css";

const LoadingSpinner = ({ message, page = false }) => (
    <div
        className={`loading-spinner${page ? " loading-spinner--page" : ""}`}
        role="status"
        aria-live="polite"
    >
        <Spinner animation="border" variant="warning" />
        {message ? (
            <p className="loading-spinner-text">{message}</p>
        ) : (
            <span className="visually-hidden">Loading...</span>
        )}
    </div>
);

export default LoadingSpinner;