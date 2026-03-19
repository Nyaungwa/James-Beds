
require("dotenv").config();

const express = require("express");
const cors = require("cors");
const products = require("./products.json");

const app = express();

app.use(cors());
app.use(express.json());

/* ======================
   TEST ROUTE
====================== */
app.get("/", (req, res) => {
    res.send("Backend is running");
});

/* ======================
   PAYFAST PAYMENT ROUTE
====================== */
app.post("/api/payfast/pay", (req, res) => {
    console.log("PayFast endpoint hit");
    console.log(req.body);

    const paymentData = {
        merchant_id: process.env.PF_MERCHANT_ID,
        merchant_key: process.env.PF_MERCHANT_KEY,

        return_url: "http://localhost:5173/success",
        cancel_url: "http://localhost:5173/cancel",
        notify_url: `${process.env.BASE_URL}/api/payfast/notify`,

        name_first: "eddy",
        email_address: "nyaungwa2025@gmail.com",

        amount: req.body.amount,
        item_name: req.body.item_name
    };

    const query = new URLSearchParams(paymentData).toString();
    const payfastUrl = "https://sandbox.payfast.co.za/eng/process";

    res.json({ url: `${payfastUrl}?${query}` });
});

/* ======================
   START SERVER (LAST)
====================== */
const PORT = 5000;
app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});


/* ======================
   Search Route
====================== */
app.get("/api/products/search", (req, res) => {
    const { q, type, size, comfort } = req.query;

    let results = products;

    if (q) {
        results = results.filter(product =>
            product.name.toLowerCase().includes(q.toLowerCase())
        );
    }

    if (type) {
        results = results.filter(product =>
            product.type.toLowerCase() === type.toLowerCase()
        );
    }

    if (size) {
        results = results.filter(product =>
            product.size.toLowerCase() === size.toLowerCase()
        );
    }

    if (comfort) {
        results = results.filter(product =>
            product.comfort.toLowerCase() === comfort.toLowerCase()
        );
    }

    res.json(results);
});

app.get("/api/products/filter", (req, res) => {
    const { type, size, comfort } = req.query;

    const results = products.filter(product =>
        (!type || product.type.toLowerCase() === type.toLowerCase()) &&
        (!size || product.size.toLowerCase() === size.toLowerCase()) &&
        (!comfort || product.comfort.toLowerCase() === comfort.toLowerCase())
    );

    res.json(results);
});






      

