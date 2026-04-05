import React from "react";
import "./Paymentpage.css";
import { useLocation, useNavigate } from "react-router-dom";

function PaymentPage() {

    const navigate = useNavigate();
    const { state } = useLocation();

    // state now contains { cartItems: [...], total: number }
    const cartItems = state?.cartItems || [];
    const total = state?.total || 0;

    const handlePayNow = () => {
        const params = new URLSearchParams({
            merchant_id: "10045300",
            merchant_key: "7qiw0fpjlf2l6",
            return_url: `${window.location.origin}/payment-success`,
            cancel_url: `${window.location.origin}/payment-cancel`,
            name_first: "Customer",
            email_address: "customer@jamescresslawn.co.za",
            amount: Number(total).toFixed(2),
            item_name: "James Cresslawn Order"
        });
        window.location.href = `https://sandbox.payfast.co.za/eng/process?${params.toString()}`;
    };

    if (!state || cartItems.length === 0) {
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

                {/* LEFT SIDE — delivery form */}
                <div className="payment-left">
                    <h3>Delivery</h3>

                    <select><option>South Africa</option></select>

                    <div className="row">
                        <input type="text" placeholder="Name" />
                        <input type="text" placeholder="Surname" />
                    </div>

                    <input type="text" placeholder="Address" />

                    <div className="row">
                        <input type="text" placeholder="City" />
                        <input type="text" placeholder="Postal Code" />
                    </div>

                    <select><option>Gauteng</option></select>

                    <input type="text" placeholder="Phone" />

                    <h3>Payment</h3>

                    <button className="pay-btn" onClick={handlePayNow}>
                        Pay Now

                    </button>
                </div>

                {/* RIGHT SIDE — now loops over cartItems */}
                <div className="payment-right">

                    {cartItems.map((item, index) => (
                        <div className="summary-item" key={index}>
                            <img src={item.image} alt={item.name} />

                            <div className="summary-text">
                                <p className="product-name">{item.name}</p>
                                <p className="delivery-text">
                                    Qty: {item.quantity} · Delivery: Free
                                </p>
                            </div>

                            <span className="product-price">
                                R{(item.price * item.quantity)}
                            </span>
                        </div>
                    ))}

                    <div className="totals">
                        <div>
                            <span>Subtotal</span>
                            <span>R{total}</span>
                        </div>
                        <div>
                            <strong>Total:   </strong>
                            <strong>R{total}</strong>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    );
}

export default PaymentPage;