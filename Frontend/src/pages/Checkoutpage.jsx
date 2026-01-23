import "./Checkoutpage.css";
import { FaTrash, FaLock } from "react-icons/fa";
import React, { useState } from "react";
import { useLocation, useParams, useNavigate } from "react-router-dom";

function CheckoutPage() {
  const { state } = useLocation(); 
  const { id } = useParams();
  const navigate = useNavigate(); 
  const [quantity, setQuantity] = useState(1);

  return (
    <div className="checkout-overlay">
      <div className="checkout-panel">

        <h3 className="checkout-title">Your Cart</h3>

        {/* Product Row */}
        <div className="checkout-item">
          <img src={state.image} alt={state.name} />

          <div className="checkout-item-info">
            <p className="item-name">{state.name}</p>
            <p className="item-price">R{state.price}</p>
          </div>
        </div>

        {/* Quantity */}
        <div className="checkout-quantity">
          <div className="qty-box">
            <button
              className="qty-btn"
              onClick={() => setQuantity(q => Math.max(1, q - 1))}
            >
              −
            </button>

            <span className="qty-number">{quantity}</span>

              <button
                className="qty-btn"
                onClick={() => setQuantity(q => q + 1)}
              >
                +
              </button>

          </div>

          <FaTrash className="delete-icon" />
        </div>

        <hr />

        {/* Subtotal */}
        <div className="checkout-summary">
          <span>Subtotal</span>
          <span>R{state.price}</span>
        </div>

        {/* Checkout Button */}
        <button
          className="checkout-btn"
          onClick={() => 
            navigate("/payment", {
              state: {
                name: state.name,
                price: state.price,
                image: state.image,
                quantity: quantity
              }
            })
          }
        >
          <FaLock /> Checkout Now
        </button>
      </div>
    </div>
  );
}

export default CheckoutPage;
