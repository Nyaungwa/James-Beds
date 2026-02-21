import "./CheckoutPage.css";
import { FaTrash, FaLock, FaArrowLeft } from "react-icons/fa";
import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

function CheckoutPage() {

    const { state } = useLocation();
    const navigate = useNavigate();

    const [quantity, setQuantity] = useState(state?.quantity || 1);

    if (!state) {
        return <p className="checkout-empty">No product in cart.</p>;
    }

    const subtotal = state.price * quantity;
    const deliveryFee = 0;
    const total = subtotal + deliveryFee;

    return (
        
        <div className="checkout-page">

            {/* Header */}
            <div className="checkout-header">
                <button
                    className="back-btn"
                    onClick={() => navigate(-1)}
                >
                    <FaArrowLeft /> Continue Shopping
                </button>
                <h2>Checkout</h2>
            </div>

            <div className="checkout-container">

                {/* LEFT SIDE – CART ITEMS */}
                <div className="checkout-items">

                    <div className="checkout-item">

                        <img
                            src={state.image}
                            alt={state.name}
                            className="item-image"
                        />

                        <div className="item-info">
                            <h3>{state.name}</h3>
                            <p className="item-price">R{state.price}</p>

                            <div className="quantity-controls">

                                <button
                                    onClick={() =>
                                        setQuantity(q => Math.max(1, q - 1))
                                    }
                                >
                                    −
                                </button>

                                <span>{quantity}</span>

                                <button
                                    onClick={() =>
                                        setQuantity(q => q + 1)
                                    }
                                >
                                    +
                                </button>

                            </div>

                        </div>

                        <div className="item-total">
                            <p>R{subtotal}</p>
                            <FaTrash className="delete-icon" />
                        </div>

                    </div>

                </div>

                {/* RIGHT SIDE – SUMMARY */}
                <div className="checkout-summary">

                    <h3>Order Summary</h3>

                    <div className="summary-row">
                        <span>Subtotal</span>
                        <span>R{subtotal}</span>
                    </div>

                    <div className="summary-row">
                        <span>Delivery</span>
                        <span>{deliveryFee === 0 ? "Free" : `R${deliveryFee}`}</span>
                    </div>

                    <hr />

                    <div className="summary-row total-row">
                        <span>Total</span>
                        <span>R{total}</span>
                    </div>

                    <button
                        className="checkout-btn"
                        onClick={() => navigate("/payment", {
                            state: {
                                ...state,
                                quantity,
                                total
                            }
                        })}
                    >
                        <FaLock /> Secure Checkout
                    </button>

                    <p className="secure-note">
                        Your payment information is processed securely.
                    </p>

                </div>

            </div>

        </div>
    );
}

export default CheckoutPage;
