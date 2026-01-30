import { BrowserRouter, Routes, Route } from "react-router-dom";
import HomePage from "./pages/Homepage";
import ProductPage from "./pages/Productpage";
import CheckoutPage from "./pages/Checkoutpage";
import PaymentPage from "./pages/Paymentpage";
import PaymentSuccess from "./pages/Paymentsuccess";
import PaymentCancel from "./pages/Paymentcancel";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/product/:id" element={<ProductPage />} />
        <Route path="/checkout" element={<CheckoutPage />} />
        <Route path="/payment" element={<PaymentPage />} />
        <Route path="/success" element={<PaymentSuccess />} />
        <Route path="/cancel" element={<PaymentCancel />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
