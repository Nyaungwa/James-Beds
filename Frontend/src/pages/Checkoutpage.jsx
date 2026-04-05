import "./Checkoutpage.css";
import {
    FaTrash,
    FaLock,
    FaArrowLeft,
    FaTag
} from "react-icons/fa";
import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

function CheckoutPage() {

    const navigate = useNavigate();
    const { state } = useLocation();

    /* ================= CART SETUP ================= */

    const initialCart = state?.items || [
        state
    ];

    const [cartItems, setCartItems] = useState(
        initialCart.map(item => ({
            ...item,
            quantity: item.quantity || 1
        }))
    );

    const [promoCode, setPromoCode] = useState("");
    const [discount, setDiscount] = useState(0);

    /* ================= CALCULATIONS ================= */

    const subtotal = cartItems.reduce(
        (acc, item) => acc + item.price * item.quantity,
        0
    );

    const deliveryFee = subtotal > 5000 ? 0 : 250;

    const total = subtotal - discount + deliveryFee;

    /* ================= HANDLERS ================= */

    const updateQuantity = (index, amount) => {
        const updated = [...cartItems];
        updated[index].quantity = Math.max(
            1,
            updated[index].quantity + amount
        );
        setCartItems(updated);
    };

    const removeItem = (index) => {
        const updated = cartItems.filter((_, i) => i !== index);
        setCartItems(updated);
    };

    const applyPromo = () => {
        if (promoCode.toLowerCase() === "luxury10") {
            setDiscount(subtotal * 0.1);
        } else {
            setDiscount(0);
        }
    };

    if (!cartItems.length) {
        return <p className="checkout-empty">Your cart is empty.</p>;
    }

    return (
        <div className="checkout-page">

            {/* ================= BRAND HEADER ================= */}
            <div className="checkout-brand"> 
                <div 
                    className="brand checkout-brand-center"
                    onClick={() => navigate("/")}
                >
                    <span className="brand-main">James</span>
                    <span className="brand-sub">Cresslawn Luxury Beds</span>
                </div>
            </div>


            {/* ================= HEADER ================= */}
            <div className="checkout-header">
                <button
                    className="back-btn"
                    onClick={() => navigate(-1)}
                >
                    <FaArrowLeft /> Continue Shopping
                </button>
                <h2>Secure Checkout</h2>
            </div>

            <div className="checkout-container">

                {/* ================= CART ITEMS ================= */}
                <div className="checkout-items">

                    {cartItems.map((item, index) => {

                        const itemTotal = item.price * item.quantity;

                        return (
                            <div
                                key={index}
                                className="checkout-item fade-in"
                            >

                                <img
                                    src={item.image}
                                    alt={item.name}
                                    className="item-image"
                                />

                                <div className="item-info">
                                    <h3>{item.name}</h3>
                                    <p className="item-price">
                                        R{item.price}
                                    </p>

                                    <div className="quantity-controls">
                                        <button
                                            onClick={() =>
                                                updateQuantity(index, -1)
                                            }
                                        >
                                            −
                                        </button>

                                        <span>{item.quantity}</span>

                                        <button
                                            onClick={() =>
                                                updateQuantity(index, 1)
                                            }
                                        >
                                            +
                                        </button>
                                    </div>
                                </div>

                                <div className="item-total">
                                    <p>R{itemTotal}</p>
                                    <FaTrash
                                        className="delete-icon"
                                        onClick={() =>
                                            removeItem(index)
                                        }
                                    />
                                </div>

                            </div>
                        );
                    })}

                </div>

                {/* ================= SUMMARY ================= */}
                <div className="checkout-summary">

                    <h3>Order Summary</h3>

                    <div className="summary-row">
                        <span>Subtotal</span>
                        <span>R{subtotal}</span>
                    </div>

                    <div className="summary-row">
                        <span>Delivery</span>
                        <span>
                            {deliveryFee === 0 ? "Free" : `R${deliveryFee}`}
                        </span>
                    </div>

                    {discount > 0 && (
                        <div className="summary-row discount-row">
                            <span>Discount</span>
                            <span>-R{discount.toFixed(0)}</span>
                        </div>
                    )}

                    <hr />

                    <div className="summary-row total-row">
                        <span>Total</span>
                        <span>R{total.toFixed(0)}</span>
                    </div>

                    {/* Promo Code */}
                    <div className="promo-section">
                        <FaTag />
                        <input
                            type="text"
                            placeholder="Promo code"
                            value={promoCode}
                            onChange={(e) =>
                                setPromoCode(e.target.value)
                            }
                        />
                        <button onClick={applyPromo}>
                            Apply
                        </button>
                    </div>

                    {/* Payment CTA */}
                    <button
                        className="checkout-btn"
                        onClick={async () => {
                            try {
                                const apiBase = import.meta.env.VITE_API_URL || "";
                                const token = localStorage.getItem("token");
                                const res = await fetch(`${apiBase}/api/orders`, {
                                    method: "POST",
                                    headers: {
                                        "Content-Type": "application/json",
                                        "Authorization": `Bearer ${token}`
                                    },
                                    body: JSON.stringify({ shippingAddress: "" })
                                });
                                const order = await res.json();
                                navigate("/payment", {
                                    state: {
                                        cartItems,
                                        total,
                                        orderId: order.orderId
                                    }
                                });
                            } catch (err) {
                                console.error("Failed to create order:", err);
                            }
                        }}
                    >
                        <FaLock /> Proceed to Payment
                    </button>

                    <p className="secure-note">
                        256-bit SSL Secure Checkout
                    </p>

                </div>

            </div>

        </div>
    );
}

export default CheckoutPage;