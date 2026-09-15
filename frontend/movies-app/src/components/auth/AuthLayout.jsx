import AuthWelcome from "./AuthWelcome";

const AuthLayout = ({ children }) => (
    <div className="auth-container">
        <div className="auth-wrapper">
            <AuthWelcome />
            {children}
        </div>
    </div>
);

export default AuthLayout;