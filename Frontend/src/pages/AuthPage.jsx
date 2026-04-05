import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./AuthPage.css";

function AuthPage() {
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState("signin");
    const [authError, setAuthError] = useState("");

    const [signInData, setSignInData] = useState({
        email: "",
        password: "",
        rememberMe: false
    });

    const [signUpData, setSignUpData] = useState({
        fullName: "",
        email: "",
        password: "",
        confirmPassword: ""
    });

    const handleSignInChange = (e) => {
        const { name, value, type, checked } = e.target;
        setSignInData((prev) => ({
            ...prev,
            [name]: type === "checkbox" ? checked : value
        }));
    };

    const handleSignUpChange = (e) => {
        const { name, value } = e.target;
        setSignUpData((prev) => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSignInSubmit = async (e) => {
        e.preventDefault();
        setAuthError("");
        const apiBase = import.meta.env.VITE_API_URL || "";
        try {
            const res = await fetch(`${apiBase}/api/auth/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email: signInData.email, password: signInData.password })
            });
            const data = await res.json();
            if (!res.ok) { setAuthError(data.message || "Invalid email or password"); return; }
            localStorage.setItem("token", data.token);
            localStorage.setItem("userName", data.fullName);
            navigate("/");
        } catch {
            setAuthError("Network error — is the backend running?");
        }
    };

    const handleSignUpSubmit = async (e) => {
        e.preventDefault();
        setAuthError("");
        if (signUpData.password !== signUpData.confirmPassword) {
            setAuthError("Passwords do not match");
            return;
        }
        const apiBase = import.meta.env.VITE_API_URL || "";
        try {
            const res = await fetch(`${apiBase}/api/auth/register`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ fullName: signUpData.fullName, email: signUpData.email, password: signUpData.password })
            });
            const data = await res.json();
            if (!res.ok) { setAuthError(data.message || "Registration failed"); return; }
            localStorage.setItem("token", data.token);
            localStorage.setItem("userName", data.fullName);
            navigate("/");
        } catch {
            setAuthError("Network error — is the backend running?");
        }
    };

    return (
        <div className="auth-page">

            {/* Reusable top panel section */}
            <header className="auth-top-panel">
                <div className="auth-top-panel-inner">
                    <div className="auth-brand" onClick={() => window.location.href = "/"}>
                        <span className="auth-brand-main">James</span>
                        <span className="auth-brand-sub">Cresslawn Luxury Beds</span>
                    </div>
                </div>
            </header>

            <main className="auth-main">
                <section className="auth-hero">
                    <div className="auth-copy">
                        <p className="auth-eyebrow">Secure customer access</p>
                        <h1 className="auth-heading">
                            Welcome back to a more refined shopping experience.
                        </h1>
                        <p className="auth-description">
                            Sign in to manage your orders, save favourites, and enjoy a
                            seamless checkout experience. New here? Create your account in
                            a few quick steps.
                        </p>

                        <div className="auth-feature-list">
                            <div className="auth-feature-item">
                                <span className="auth-feature-title">Fast checkout</span>
                                <span className="auth-feature-text">
                                    Save your details for a smoother purchase flow.
                                </span>
                            </div>

                            <div className="auth-feature-item">
                                <span className="auth-feature-title">Order visibility</span>
                                <span className="auth-feature-text">
                                    Track current and past purchases in one place.
                                </span>
                            </div>

                            <div className="auth-feature-item">
                                <span className="auth-feature-title">Trusted access</span>
                                <span className="auth-feature-text">
                                    Secure sign in designed for a premium retail experience.
                                </span>
                            </div>
                        </div>
                    </div>

                    <div className="auth-card-wrapper">
                        <div className="auth-card">
                            <div className="auth-card-header">
                                <p className="auth-card-kicker">Account access</p>
                                <h2 className="auth-card-title">
                                    {activeTab === "signin" ? "Sign in" : "Create account"}
                                </h2>
                                <p className="auth-card-subtitle">
                                    {activeTab === "signin"
                                        ? "Enter your details to continue securely."
                                        : "Create your account to save preferences and order faster."}
                                </p>
                            </div>

                            <div
                                className="auth-switch"
                                role="tablist"
                                aria-label="Authentication options"
                            >
                                <button
                                    type="button"
                                    className={`auth-switch-btn ${activeTab === "signin" ? "active" : ""}`}
                                    onClick={() => setActiveTab("signin")}
                                    role="tab"
                                    aria-selected={activeTab === "signin"}
                                >
                                    Sign In
                                </button>

                                <button
                                    type="button"
                                    className={`auth-switch-btn ${activeTab === "signup" ? "active" : ""}`}
                                    onClick={() => setActiveTab("signup")}
                                    role="tab"
                                    aria-selected={activeTab === "signup"}
                                >
                                    Sign Up
                                </button>
                            </div>

                            {activeTab === "signin" ? (
                                <form className="auth-form" onSubmit={handleSignInSubmit} noValidate>
                                    <div className="auth-field">
                                        <label htmlFor="signin-email">Email</label>
                                        <input
                                            id="signin-email"
                                            name="email"
                                            type="email"
                                            placeholder="Enter your email address"
                                            value={signInData.email}
                                            onChange={handleSignInChange}
                                            autoComplete="email"
                                        />
                                        <small className="auth-helper">
                                            Use the email linked to your account.
                                        </small>
                                    </div>

                                    <div className="auth-field">
                                        <label htmlFor="signin-password">Password</label>
                                        <input
                                            id="signin-password"
                                            name="password"
                                            type="password"
                                            placeholder="Enter your password"
                                            value={signInData.password}
                                            onChange={handleSignInChange}
                                            autoComplete="current-password"
                                        />
                                    </div>

                                    <div className="auth-row auth-row-between">
                                        <label className="auth-checkbox">
                                            <input
                                                type="checkbox"
                                                name="rememberMe"
                                                checked={signInData.rememberMe}
                                                onChange={handleSignInChange}
                                            />
                                            <span>Remember me</span>
                                        </label>

                                        <a href="/" className="auth-link">
                                            Forgot Password?
                                        </a>
                                    </div>

                                    {authError && <p className="auth-error-msg">{authError}</p>}
                                    <button type="submit" className="auth-submit-btn">
                                        Sign In
                                    </button>

                                    <p className="auth-footnote">
                                        By continuing, you agree to secure account access and
                                        responsible use of your account.
                                    </p>
                                </form>
                            ) : (
                                <form className="auth-form" onSubmit={handleSignUpSubmit} noValidate>
                                    <div className="auth-field">
                                        <label htmlFor="signup-fullname">Full Name</label>
                                        <input
                                            id="signup-fullname"
                                            name="fullName"
                                            type="text"
                                            placeholder="Enter your full name"
                                            value={signUpData.fullName}
                                            onChange={handleSignUpChange}
                                            autoComplete="name"
                                        />
                                    </div>

                                    <div className="auth-field">
                                        <label htmlFor="signup-email">Email</label>
                                        <input
                                            id="signup-email"
                                            name="email"
                                            type="email"
                                            placeholder="Enter your email address"
                                            value={signUpData.email}
                                            onChange={handleSignUpChange}
                                            autoComplete="email"
                                        />
                                    </div>

                                    <div className="auth-field-grid">
                                        <div className="auth-field">
                                            <label htmlFor="signup-password">Password</label>
                                            <input
                                                id="signup-password"
                                                name="password"
                                                type="password"
                                                placeholder="Create a password"
                                                value={signUpData.password}
                                                onChange={handleSignUpChange}
                                                autoComplete="new-password"
                                            />
                                        </div>

                                        <div className="auth-field">
                                            <label htmlFor="signup-confirm-password">
                                                Confirm Password
                                            </label>
                                            <input
                                                id="signup-confirm-password"
                                                name="confirmPassword"
                                                type="password"
                                                placeholder="Confirm your password"
                                                value={signUpData.confirmPassword}
                                                onChange={handleSignUpChange}
                                                autoComplete="new-password"
                                            />
                                        </div>
                                    </div>

                                    <small className="auth-helper auth-helper-block">
                                        Choose a strong password with a mix of letters, numbers,
                                        and symbols.
                                    </small>

                                    {authError && <p className="auth-error-msg">{authError}</p>}
                                    <button type="submit" className="auth-submit-btn">
                                        Create Account
                                    </button>

                                    <p className="auth-footnote">
                                        Creating an account helps you save your details and move
                                        through checkout more efficiently.
                                    </p>
                                </form>
                            )}
                        </div>
                    </div>
                </section>
            </main>

            {/* Reusable footer section */}
            <footer className="auth-footer">
                <div className="auth-footer-content">
                    <div className="auth-footer-column">
                        <h4>James Cresslawn</h4>
                        <p>
                            Premium sleep solutions built around comfort, durability,
                            and refined design.
                        </p>
                    </div>

                    <div className="auth-footer-column">
                        <h4>Customer Care</h4>
                        <p>Contact Us</p>
                        <p>Shipping & Delivery</p>
                        <p>Returns Policy</p>
                    </div>

                    <div className="auth-footer-column">
                        <h4>Company</h4>
                        <p>About Us</p>
                        <p>Privacy Policy</p>
                        <p>Terms & Conditions</p>
                    </div>

                    <div className="auth-footer-column">
                        <h4>Account</h4>
                        <p>Sign In</p>
                        <p>Create Account</p>
                        <p>Order Support</p>
                    </div>
                </div>

                <div className="auth-footer-bottom">
                    © 2026 James Cresslawn Luxury Beds. All rights reserved.
                </div>
            </footer>

        </div>
    );
}

export default AuthPage;