import "./ProductPage.css";
import { FaTruck } from "react-icons/fa";
import React, { useState } from "react";
import { useLocation, useParams } from "react-router-dom";


function ProductPage() {
    const [quantity, setQuantity] = useState(1);
    const { state } = useLocation(); 
    const { id } = useParams(); 

    return (
        <div className="product-page">

            {/* Main product section */}
            <div className="product-container">

                {/* LEFT: Image */}
                <div className="product-image">
                    <img src={state.image} alt={state.name} />
                </div>

                {/* RIGHT: Details */}
                <div className="product-details">
                    <h1>{state.name}</h1>
                    <p className="product-price">R{state.price}</p>

                    {/* Quantity selector */}
                    <div className="quantity-box">
                        <label>Quantity</label>
                        <select
                            value={quantity}
                            onChange={(e) => setQuantity(e.target.value)}
                        >
                            {[1,2,3,4,5].map(num => (
                                <option key={num} value={num}>{num}</option>
                            ))}
                        </select>
                    </div>

                    {/* Add to cart */}
                    <button className="add-to-cart-btn">
                        Add to Cart
                    </button>

                    {/* Delivery info */}
                    <div className="delivery-info">
                        <FaTruck />
                        <span>2–3 working days delivery</span>
                    </div>
                </div>
            </div>

            {/* FOOTER (reuse same structure as homepage) */}
            <footer className="footer">
                <div className="footer-content">
                    <div className="footer-column">
                        <h4>Get In Touch</h4>
                        <p>Email: cjnr@gmail.com</p>
                        <p>WhatsApp: 067 9567</p>
                        <p>Phone: 058 5885</p>
                    </div>

                    <div className="footer-column">
                        <h4>Product</h4>
                        <p>Beds</p>
                        <p>Mattresses</p>
                        <p>Headboards</p>
                    </div>

                    <div className="footer-column">
                        <h4>Company Info</h4>
                        <p>Terms & Conditions</p>
                        <p>Privacy Policy</p>
                        <p>Contact Us</p>
                    </div>
                </div>
            </footer>

        </div>
    );
}

export default ProductPage;
