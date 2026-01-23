import React from "react";
import "./PaymentPage.css";
import { useLocation, useParams, useNavigate } from "react-router-dom";

function PaymentPage() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { state } = useLocation();


  return (
    <div className="payment-page">

      {/* LEFT */}
      <div className="payment-left">

        <h3>Delivery</h3>

        <select>
          <option>South Africa</option>
        </select>

        <div className="row">
          <input placeholder="Name" />
          <input placeholder="Surname" />
        </div>

        <input placeholder="Address" />
        
        <div className="row">
          <input placeholder="City" />
          <input placeholder="Postal Code" />
        </div>

        <select>
          <option>Gauteng</option>
        </select>

        <input placeholder="Phone" />

        <h3>Payment</h3>

        <div className="payment-box">
          <input placeholder="Card Number" />

          <div className="row">
            <input placeholder="Expiry Date" />
            <input placeholder="CVV" />
          </div>

          <input placeholder="Name on Card" />

          <button className="pay-btn">Pay Now</button>
        </div>
      </div>

      {/* RIGHT */}
      <div className="payment-right">
        <div className="summary-item">
          <img src={state.image} alt={state.name} />
          <p>{state.name}</p>
          <span>R{state.price}</span>
        </div>

        <p className="delivery">Delivery: Drop off (Free)</p>

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
  );
}

export default PaymentPage;
