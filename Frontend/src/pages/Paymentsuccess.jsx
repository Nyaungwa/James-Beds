import "./PaymentSuccess.css";
import { useNavigate } from "react-router-dom";

function PaymentSuccess() {
  const navigate = useNavigate();

  const today = new Date().toLocaleDateString();

  return (
    <div className="payment-container">
    
                 <div className="payment-top-panel">
                <div className="brand">
                    <span className="brand-main">James</span>
                    <br />
                    <span className="brand-sub">Best Affordable Beds</span>
                </div>
            </div>

      {/* Content */}
      <div className="payment-content">
        <div className="status-icon success">✔</div>
        <h2 className="status-title success">Payment Successful!</h2>

        <p className="status-subtitle">
          Thank you, your payment was successfully processed
        </p>

        {/* Receipt box */}
        <div className="receipt-box">
          <div className="receipt-row">
            <span>Amount</span>
            <span>R 2,999.00</span>
          </div>

          <hr />

          <div className="receipt-row">
            <span>Product Name:</span>
            <span>Firm Double Bed Set</span>
          </div>
          <div className="receipt-row">
            <span>Transaction ID:</span>
            <span>PF123456789</span>
          </div>
          <div className="receipt-row">
            <span>Date:</span>
            <span>{today}</span>
          </div>
        </div>

        <button className="ok-btn" onClick={() => navigate("/")}>
          OK
        </button>
      </div>
    </div>
  );
}

export default PaymentSuccess;
