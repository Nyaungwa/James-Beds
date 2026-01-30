import "./Paymentcancel.css";
import { useNavigate } from "react-router-dom";

function PaymentCancel() {
  const navigate = useNavigate();

  return (
    <div className="payment-page">
      {/* Top panel */}
      <div className="top-panel">
        <h2>Payment</h2>
      </div>

      {/* Content */}
      <div className="payment-content">
        <div className="status-icon cancel">✖</div>
        <h2 className="status-title cancel">Payment Unsuccessful</h2>

        <p className="cancel-message">
          We are sorry, we were unable to process your payment.
        </p>

        <button className="ok-btn cancel" onClick={() => navigate("/")}>
          OK
        </button>
      </div>
    </div>
  );
}

export default PaymentCancel;
