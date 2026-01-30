import "./Paymentcancel.css";
import { useNavigate } from "react-router-dom";

function PaymentCancel() {
  const navigate = useNavigate();

  return (
    <div className="payment-container">

                  <div className="payment-top-panel">
                <div className="brand">
                    <span className="brand-main">James</span>
                    <br />
                    <span className="brand-sub">Best Affordable Beds</span>
                </div>
            </div>
 
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
