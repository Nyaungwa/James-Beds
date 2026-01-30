import React from "react";
import "./PaymentPage.css";
import { useLocation, useNavigate } from "react-router-dom";

function PaymentPage() {

    const navigate = useNavigate();
    const { state } = useLocation();

    const handlePayNow = async () => {
         console.log("Pay Now clicked");
        try {
            const response = await fetch("http://localhost:5000/api/payfast/pay", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    amount: Number(state.price).toFixed(2),
                    item_name: state.name
                })
            });

            const data = await response.json();

            window.location.href = data.url;

        } catch (error) {
            console.error("Payment error:", error);
        } 
    };


    if (!state) {
        return <p style={{ padding: "40px" }}>No payment details found.</p>;
    }

    return (
        <div className="payment-container">

            <div className="payment-top-panel">
                <div className="brand">
                    <span className="brand-main">James</span>
                    <br />
                    <span className="brand-sub">Best Affordable Beds</span>
                </div>
            </div>

    
            <div className="payment-page">


                <div className="payment-left">

                    <h3>Delivery</h3>

                    <select>
                        <option>South Africa</option>
                    </select>

                    <div className="row">
                        <input type="text" placeholder="Name" />
                        <input type="text" placeholder="Surname" />
                    </div>

                    <input type="text" placeholder="Address" />

                    <div className="row">
                        <input type="text" placeholder="City" />
                        <input type="text" placeholder="Postal Code" />
                    </div>

                    <select>
                        <option>Gauteng</option>
                    </select>

                    <input type="text" placeholder="Phone" />

                    <h3>Payment</h3>

                    <button className="pay-btn" onClick = {handlePayNow}>
                        Pay with 
                        <img
                            src = "/src/assets/PayFast-logo.png"
                            alt = "PayFast"
                            className = "payFast-logo"
                        />
                    </button>
                </div>

                {/* RIGHT SIDE */}
                <div className="payment-right">

                    <div className="summary-item">
                        <img src={state.image} alt={state.name} />

                        <div className="summary-text">
                            <p className="product-name">
                                {state.name}
                            </p>

                            <p className="delivery-text">
                                Delivery: Drop off (Free)
                            </p>
                        </div>

                        <span className="product-price">
                            R{state.price}
                        </span>
                    </div>

                    <div className="totals">
                        <div>
                            <span>Subtotal</span>
                            <span>R{state.price}</span>
                        </div>

                        <div>
                            <strong>Total</strong>
                            <strong>R{state.price}</strong>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    );
}

export default PaymentPage;
