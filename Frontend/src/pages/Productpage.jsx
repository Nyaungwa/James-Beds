import "./ProductPage.css";
import { FaTruck } from "react-icons/fa";
import React, { useState } from "react";
import { useLocation, useParams, useNavigate } from "react-router-dom";



function ProductPage() {
    const [quantity, setQuantity] = useState(1);
    const { state } = useLocation(); 
    const { id } = useParams();
    const navigate = useNavigate(); 

    if (!state) {
        return <p>Product not found</p>;
    }

    return (
        <div className="product-page">

            <div className="product-container">

                <div className="product-image">
                    <img src={state.image} alt={state.name} />
                </div>

                <div className="product-details">
                    <h1>{state.name}</h1>
                    <p className="product-price">R{state.price}</p>

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

                    <button 
                        className="add-to-cart-btn"
                        onClick={() => 
                            navigate("/checkout", {
                                state: {
                                    name: state.name,
                                    price: state.price,
                                    image: state.image,
                                    quantity: quantity
                                }
                            })
                        }

                    >
                        Add to Cart
                    </button>

                    {/* Delivery info */}
                    <div className="delivery-info">
                        <FaTruck />
                        <span>2–3 Working days delivery</span>
                    </div>
                </div>
            </div>

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
