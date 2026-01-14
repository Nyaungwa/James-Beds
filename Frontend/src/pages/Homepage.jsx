import React from "react";
import "./Homepage.css";
import {FaUser, FaShoppingCart, FaSearch} from "react-icons/fa";

function HomePage() {
    return (
        <div className = "homepage-cointaner">

            {/*Top panel*/}
            <div className = "top-panel">

                {/* Company Name */}
                <div className = "brand">
                    <span className="brand-main">James</span>
                <br />
                <span className="brand-sub">Best Affordable Beds</span>
                </div>

                {/* Search Bar*/}
                <div className = "search-selection">
                    <div className = "search-box">
                        <input 
                        type = "text"
                        className = "search-input"
                        placeholder = "Search..."
                        />
                        <FaSearch className = "search-icon"/>
                    </div>
                </div>

                {/* Icons */}
                <div className = "icon-selection">
                    {/* User */}
                    <div className = "icon-items">
                        <FaUser className = "icon" />
                        <span className = "icon-text">Login</span>
                    </div>
                    
                    {/* Shopping Cart */}
                    <div className = "icon-items">
                        <div className= "cart-icon-wrapper">
                            <FaShoppingCart className= "icon"/>
                            <span className = "cart-count">0</span>
                        </div>
                        <span className ="icon-text">Cart</span>
                    </div>
                </div>
            </div>
                
     
        {/* Search Beds Background */}
        <div className = "filterBox-background">
            <div className="filter-box">
                <h2 className="filter-title">Beds for Sale</h2>
                <p className="filter-subtitle">
                    Let's see what you are comfortable with
                </p>

                <div className="filter-row center">
                    <select className="filter-input small">
                    <option>Type</option>
                    <option>Bed</option>
                    <option>Mattress</option>
                    <option>Headboard</option>
                    </select>
                </div>

                <div className="filter-row split">
                    <select className="filter-input small">
                        <option>Size</option>
                        <option>Single</option>
                        <option>Double</option>
                        <option>Queen</option>
                        <option>King</option>
                    </select>

                    <select className="filter-input small">
                        <option>Comfort</option>
                        <option>Foam</option>
                        <option>Spring</option>
                        <option>Hybrid</option>
                    </select>
                </div>
                <button className="filter-button center">
                    Search
                </button>
            </div>

        </div>
      
        {/*Products for sale Section*/}
        <h2 className ="beds-section-name">Beds for sale</h2>
        <div className = "beds-section">
            <div className = "beds-card1">
                <img
                   src="/src/assets/go-ultra-105-grey-base-_1_.jpg"
                   alt = "Luxury Beds"
                   className = "bed-images"
                />
                <div className = "bed-info">
                    <h3>Firm Single Bed Set</h3>
                    <p className = "price"> R1999 </p>
                    <button className = "add-cart">Add to Cart</button>    
                </div>                
            </div>

            <div className = "beds-card2">
                <img
                   src="/src/assets/go-ultra-105-grey-base-_1_.jpg"
                   alt = "Luxury Beds"
                   className = "bed-images"
                />
                <div className = "bed-info">
                    <h3>Firm Double Bed Set</h3>
                    <p className = "price"> R2999 </p>
                    <button className = "add-cart">Add to Cart</button>    
                </div>                
            </div>

            <div className = "beds-card3">
                <img
                   src="/src/assets/willowbridge-grey-fluted-base-_2_.jpg"
                   alt = "Luxury Beds"
                   className = "bed-images"
                />
                <div className = "bed-info">
                    <h3>Firm Queen bed set</h3>
                    <p className = "price"> R3999 </p>
                    <button className = "add-cart">Add to Cart</button>    
                </div>                
            </div>

            <div className = "beds-card4">
                <img
                   src="/src/assets/Dream-Safe-min.png"
                   alt = "Luxury Beds"
                   className = "bed-images"
                />
                <div className = "bed-info">
                    <h3>Firm King bed set</h3>
                    <p className = "price"> R5999 </p>
                    <button className = "add-cart">Add to Cart</button>    
                </div>                
            </div>
        </div>
        <h2 className ="beds-section-name">Mattress for sale</h2>

        <div className= "mattress-section">
            <div className = "beds-card1">
                <img
                   src="/src/assets/Strandmattress-Graduate-Duo-Mattress.jpg.webp"
                   alt = "Luxury mattresses"
                   className = "beds-images"
                />
                <div className = "bed-info">
                    <h3>Single Mattress</h3>
                    <p className = "price"> R2999 </p>
                    <button className = "add-cart">Add to Cart</button>    
                </div>                
            </div>

            <div className = "beds-card2">
                <img
                   src="/src/assets/Strandmattress-Graduate-Lux-Mattress.jpg.webp"
                   alt = "Luxury Beds"
                   className = "bed-images"
                />
                <div className = "bed-info">
                    <h3>Double Mattress</h3>
                    <p className = "price"> R3999 </p>
                    <button className = "add-cart">Add to Cart</button>    
                </div>                
            </div>

            <div className = "beds-card3">
                <img
                   src="/src/assets/Weightmaster-Mattress.jpg.webp"
                   alt = "Luxury Beds"
                   className = "bed-images"
                />
                <div className = "bed-info">
                    <h3>Queen Mattress</h3>
                    <p className = "price"> R4999 </p>
                    <button className = "add-cart">Add to Cart</button>    
                </div>                
            </div>

            <div className = "beds-card4">
                <img
                   src="/src/assets/Sealy_Lannister_Extra_Firm_Mattress-1.jpg.webp"
                   alt = "Luxury Beds"
                   className = "bed-images"
                />
                <div className = "bed-info">
                    <h3>Firm King bed set</h3>
                    <p className = "price"> R10999 </p>
                    <button className = "add-cart">Add to Cart</button>    
                </div>                
            </div>
        </div>
    </div>
    );
}
export default HomePage;

