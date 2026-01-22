import React from "react";
import "./CheckoutPage.css";
import { FaTrash, FaLock } from "react-icons/fa";
import { useNavigate } from "react-router-dom";

function CheckoutPage() {
  const navigate = useNavigate();

  const product = {
    name: "Firm Single Bed Set",
    price: 1999,
    image: "/src/assets/Single.jpeg",
    quantity: 1
  };

  return (
    <div className="checkout-overlay">
      <div className="checkout-panel">

        <h3 className="checkout-title">Your Cart</h3>

        {/* Product Row */}
        <div className="checkout-item">
          <img src={product.image} alt={product.name} />

          <div className="checkout-item-info">
            <p className="item-name">{product.name}</p>
            <p className="item-price">R{product.price}</p>
          </div>
        </div>

        {/* Quantity */}
        <div className="checkout-quantity">
          <button>-</button>
          <span>1</span>
          <button>+</button>

          <FaTrash className="delete-icon" />
        </div>

        <hr />

        {/* Subtotal */}
        <div className="checkout-summary">
          <span>Subtotal</span>
          <span>R{product.price}</span>
        </div>

        {/* Checkout Button */}
        <button
          className="checkout-btn"
          onClick={() => navigate("/payment")}
        >
          <FaLock /> Checkout Now
        </button>
      </div>
    </div>
  );
}

export default CheckoutPage;
