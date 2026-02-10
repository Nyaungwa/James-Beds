import React, { useState, useEffect } from "react";
import "./Homepage.css";
import {FaUser, FaShoppingCart, FaSearch, FaFacebookF, FaInstagram} from "react-icons/fa";
import { useNavigate } from "react-router-dom";



function HomePage() {

    const navigate = useNavigate();
    const [search, setSearch] = useState("");
    const [results, setResults] = useState([]);
    const [showSearchBar, setShowSearchBar] = useState(false);

    useEffect(() => {
        if (search.length < 2) {
            setResults([]);
            return;
        }
        fetch(`/api/products/search?q=${search}`)
            .then(res => res.json())
            .then(data => setResults(data));

        }, [search]);

    return (
        <div className = "homepage-container">

            <div className = "top-panel">

                <div className = "brand">
                    <span className="brand-main">James</span>
                <br />
                <span className="brand-sub">Cresslawn Luxury Beds</span>
                </div>

                {/* Search Bar*/}
                <div className = "search-selection">
                    <div className = "search-box">
                        <input 
                        type = "text"
                        className = "search-input"
                        placeholder = "Search..."
                        value = {search}
                        onChange={(e) => setSearch(e.target.value)}
                        />

                        {results.length > 0 && (
                            <div className="search-results-box">
                                {results
                                    .filter(
                                        (item, index, self) =>
                                            index === self.findIndex(p => p.id === item.id)
                                    )
                                    .map(item => (
                                    <div
                                        key={item.id}
                                        className="search-result-item"
                                        onClick={() =>
                                            navigate(`/product/${item.id}`, { state: item })
                                        }
                                    >
                                        <img 
                                            src={item.image} 
                                            alt={item.name}
                                            style={{
                                                width: "60px",
                                                height: "45px",
                                                objectFit: "cover",
                                                borderRadius: "5px"
                                            }}
                                        />
                                        <div className="search-result-info">
                                            <p>{item.name}</p>
                                            <span>R{item.price}</span>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                        <FaSearch className = "search-icon"/>
                    </div>
                </div>

                {/* Icons */}
                <div className = "icon-selection">

                    <div 
                        className = "mobile-only-search"
                        onClick={() => setShowSearchBar(!showSearchBar)}
                    >
                        <FaSearch className="mobile-icon" />

                    </div>
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
                {showSearchBar && (
                        <div className="mobile-search-box">
                            <input
                                type="text"
                                className="mobile-search-input"
                                placeholder="Search..."
                                value={search}
                                onChange={(e) => setSearch(e.target.value)}   
                            />
                            {results.length > 0 && (
                            <div className="mobile-search-results-box">
                                {results
                                    .filter(
                                        (item, index, self) =>
                                            index === self.findIndex(p => p.id === item.id)
                                    )
                                    .map(item => (
                                    <div
                                        key={item.id}
                                        className="mobile-search-result-item"
                                        onClick={() =>
                                            navigate(`/product/${item.id}`, { state: item })
                                        }
                                    >
                                        <img 
                                            src={item.image} 
                                            alt={item.name}
                                            style={{
                                                width: "60px",
                                                height: "45px",
                                                objectFit: "cover",
                                                borderRadius: "5px"
                                            }}
                                        />
                                        <div className="mobile-search-result-info">
                                            <p>{item.name}</p>
                                            <span>R{item.price}</span>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                            <FaSearch className="mobile-search-icon" />
                        </div>
                    )
                }
                
     
        {/* Search Beds Background */}
        <div className = "filterBox-background">
             <div className="filterBox-content">
                <h2 className="filter-title">Beds for Sale</h2>
                <p className="filter-subtitle">
                    Let's see what you are comfortable with
                </p>
                <div className="filter-box">
                    <div className="filter-row inline">
                        <select className="filter-input small">
                            <option>Type</option>
                            <option>Bed</option>
                            <option>Mattress</option>
                            <option></option>
                        </select>

                        <select className="filter-input small">
                            <option>Size</option>
                            <option>Single</option>
                            <option>3/4</option>
                            <option>Double</option>
                            <option>Queen</option>
                            <option>King</option>
                        </select>

                        <select className="filter-input small">
                            <option>Comfort</option>
                            <option>Pillow Top</option>
                            <option>Box Top</option>
                            <option>Euro Top</option>
                        </select>

                        <div className="filter-row center">
                            <button className="filter-button small">
                                Search
                            </button>
                        </div>
                    
                    </div>

                </div>

            </div>
        </div>
      
        {/*Products for sale Section*/}
        <h2 className ="beds-section-name">Beds for sale</h2>
        <div className = "beds-section">
            <div className = "beds-card">
                <img
                   src="src/assets/Double.jpeg"
                   alt = "Luxury Beds"
                   className = "bed-images"
                />
                <div className = "bed-info">
                    <h3>Pillow Top Single Bed Set</h3>
                    <p className = "price"> R2599 </p>
                    <button className = "add-cart"
                    onClick={() =>
                        navigate("/product/1", {
                            state: {
                                name: "Pillow Top Single Bed Set",
                                price: 2599,
                                image: "src/assets/Double.jpeg"
                            }
                        })
                    }                  

                    > View Product 
                    </button>    
                </div>                
            </div>

            <div className = "beds-card">
                <img
                   src="/src/assets/Double.jpeg"
                   alt = "Luxury Beds"
                   className = "bed-images"
                />
                <div className = "bed-info">
                    <h3>Box Top Double Bed Set</h3>
                    <p className = "price"> R2999 </p>
                    <button className = "add-cart"
                    onClick={() =>
                        navigate("/product/2", {
                            state: {
                                name: "Firm Double Bed Set",
                                price: 2999,
                                image: "/src/assets/Double.jpeg"
                            }
                        })
                    }            
                    >Add to Cart
                    </button>    
                </div>                
            </div>

            <div className = "beds-card">
                <img
                   src="/src/assets/Queen.jpeg"
                   alt = "Luxury Beds"
                   className = "bed-images"
                />
                <div className = "bed-info">
                    <h3>Firm Queen bed set</h3>
                    <p className = "price"> R3999 </p>
                    <button className = "add-cart"
                    onClick={() =>
                        navigate("/product/3", {
                            state: {
                                name: "Firm Queen bed set",
                                price: 3999,
                                image: "/src/assets/Queen.jpeg"
                            }
                        })
                    }            
                    >Add to Cart
                    </button>    
                </div>                
            </div>

            <div className = "beds-card">
                <img
                   src="/src/assets/King.jpeg"
                   alt = "Luxury Beds"
                   className = "bed-images"
                />
                <div className = "bed-info">
                    <h3>Firm King bed set</h3>
                    <p className = "price"> R5999 </p>
                    <button className = "add-cart"
                    onClick={() =>
                        navigate("/product/4", {
                            state: {
                                name: "Firm King bed set",
                                price: 5999,
                                image: "/src/assets/King.jpeg"
                            }
                        })
                    }            
                    >Add to Cart
                    </button>    
                </div>                
            </div>
        </div>
        <h2 className ="beds-section-name">Mattress for sale</h2>

        <div className= "mattress-section">
            <div className = "beds-card">
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

            <div className = "beds-card">
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

            <div className = "beds-card">
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

            <div className = "beds-card">
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

        <footer className="footer">
            <div className="footer-content">

                <div className="footer-column">
                    <h4>Get In Touch</h4>
                    <p>Email: c@gmail.com</p>
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

            <div className="follow-us-bottom">
                <h4>Follow Us</h4>
                
                <div className="social-icons">
                    <div className="icon-items">
                        <FaFacebookF className="icon" />
                        <FaInstagram className="icon" />
                    </div>
                </div>
            </div>
        </footer>
    </div>
    );
}
export default HomePage;
