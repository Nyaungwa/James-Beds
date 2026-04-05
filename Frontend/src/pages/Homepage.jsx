import React, { useState, useEffect } from "react";
import "./Homepage.css";
import {FaUser, FaShoppingCart, FaSearch, FaFacebookF, FaInstagram} from "react-icons/fa";
import { useNavigate } from "react-router-dom";



function HomePage() {

    const navigate = useNavigate();
    const [search, setSearch] = useState("");
    const [results, setResults] = useState([]);
    const [showSearchBar, setShowSearchBar] = useState(false);
    const [type, setType] = useState("");
    const [size, setSize] = useState("");
    const [comfort, setComfort] = useState("");
    const [filteredProducts, setFilteredProducts] = useState([]);
    const [hasSearched, setHasSearched] = useState(false);


    const handleFilterSearch = () => {
        const apiBase = import.meta.env.VITE_API_URL || "";
        fetch(`${apiBase}/api/products/filter?type=${type}&size=${size}&comfort=${comfort}`)
            .then(res => res.json())
            .then(data => {
                console.log(data);
                setFilteredProducts(data);
                setHasSearched(true);
            })
            .catch(err => console.error(err));
    };

    useEffect(() => {
        if (search.length < 2) {
            setResults([]);
            return;
        }
        const apiBase = import.meta.env.VITE_API_URL || "";
        fetch(`${apiBase}/api/products/search?q=${search}`)
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
                        <select 
                            className="filter-input small"
                            value={type}
                            onChange={(e) => setType(e.target.value)}
                        >
                            <option value="">Type</option>
                            <option value="bed">Bed</option>
                            <option value="mattress">Mattress</option>
                        </select>

                        <select 
                            className="filter-input small"
                            value={size}
                            onChange={(e) => setSize(e.target.value)}
                        >
                            <option value ="">Size</option>
                            <option value ="single">Single</option>
                            <option value ="3/4">3/4</option>
                            <option value ="double">Double</option>
                            <option value = "queen">Queen</option>
                            <option value = "king">King</option>
                        </select>

                         <select 
                            className="filter-input small"
                            value={comfort}
                            onChange={(e) => setComfort(e.target.value)}
                        >
                            <option value="">Comfort</option>
                            <option value= "Pillow Top">Pillow Top</option>
                            <option value ="Box Top">Box Top</option>
                            <option value = "Euro Top">Euro Top</option>
                        </select>

                        <div className="filter-row center">
                            <button 
                                className="filter-button small"
                                onClick={handleFilterSearch}
                            >
                                Search
                            </button>
                        </div>
                    
                    </div>

                </div>

            </div>
        </div>
        {/* ================= PRODUCTS FOR SALE SECTION ================= */}

<h2 className="beds-section-name">Beds for Sale</h2>

{hasSearched ? (
  <div className="beds-section">
    {filteredProducts.length > 0 ? (
      filteredProducts.map((product) => {
        const oldPrice = product.oldPrice || product.price + 800;
        const discountPercent = Math.round(
          ((oldPrice - product.price) / oldPrice) * 100
        );

        return (
          <div key={product.id} className="beds-card">
            <div className="discount-badge">-{discountPercent}%</div>

            <img
              src={product.image}
              alt={product.name}
              className="bed-images"
            />

            <div className="bed-info">
              <h3>{product.name}</h3>

              <div className="price-container">
                <span className="old-price">R{oldPrice}</span>
                <span className="new-price">R{product.price}</span>
              </div>
            </div>
          </div>
        );
      })
    ) : (
      <p>No products found</p>
    )}
  </div>
) : (
  <div className="beds-section">
    {[
      {
        id: 1,
        name: "Pillow Top Single Bed Set",
        price: 2599,
        oldPrice: 3299,
        image: "src/assets/Queen.jpeg",
      },
      {
        id: 2,
        name: "Box Top Double Bed Set",
        price: 2999,
        oldPrice: 3799,
        image: "/src/assets/Double.jpeg",
      },
      {
        id: 3,
        name: "Firm Queen Bed Set",
        price: 3999,
        oldPrice: 4899,
        image: "/src/assets/Queen.jpeg",
      },
      {
        id: 4,
        name: "Firm King Bed Set",
        price: 5999,
        oldPrice: 7299,
        image: "/src/assets/Queen.jpeg",
      },
    ].map((product) => {
      const discountPercent = Math.round(
        ((product.oldPrice - product.price) / product.oldPrice) * 100
      );

      return (
        <div key={product.id} className="beds-card">
          <div className="discount-badge">-{discountPercent}%</div>

          <img
            src={product.image}
            alt={product.name}
            className="bed-images"
          />

          <div className="bed-info">
            <h3>{product.name}</h3>

            <div className="price-container">
              <span className="old-price">R{product.oldPrice}</span>
              <span className="new-price">R{product.price}</span>
            </div>

            <button
              className="add-cart"
              onClick={() =>
                navigate(`/product/${product.id}`, { state: product })
              }
            >
              View Product
            </button>
          </div>
        </div>
      );
    })}
  </div>
)}

{/* ================= MATTRESS SECTION ================= */}

<h2 className="beds-section-name">Mattresses for Sale</h2>

<div className="mattress-section">
  {[
    {
      id: 5,
      name: "Single Mattress",
      price: 2999,
      oldPrice: 3599,
      image:
        "/src/assets/Queen.jpeg",
    },
    {
      id: 6,
      name: "Double Mattress",
      price: 3999,
      oldPrice: 4699,
      image:
        "/src/assets/Queen.jpeg",
    },
    {
      id: 7,
      name: "Queen Mattress",
      price: 4999,
      oldPrice: 5799,
      image: "/src/assets/Queen.jpeg",
    },
    {
      id: 8,
      name: "King Mattress",
      price: 10999,
      oldPrice: 12499,
      image:
        "/src/assets/Queen.jpeg",
    },
  ].map((product) => {
    const discountPercent = Math.round(
      ((product.oldPrice - product.price) / product.oldPrice) * 100
    );

    return (
      <div key={product.id} className="beds-card">
        <div className="discount-badge">-{discountPercent}%</div>

        <img
          src={product.image}
          alt={product.name}
          className="bed-images"
        />

        <div className="bed-info">
          <h3>{product.name}</h3>

          <div className="price-container">
            <span className="old-price">R{product.oldPrice}</span>
            <span className="new-price">R{product.price}</span>
          </div>

          <button
            className="add-cart"
            onClick={() =>
              navigate(`/product/${product.id}`, { state: product })
            }
          >
            View Product
          </button>
        </div>
      </div>
    );
  })}
</div>

{/* ================= PROFESSIONAL FOOTER ================= */}

<footer className="footer premium-footer">
  <div className="footer-content">

    <div className="footer-column">
      <h4>Get In Touch</h4>
      <p>Email: support@jamescresslawn.co.za</p>
      <p>WhatsApp: 067 956 0000</p>
      <p>Phone: 058 588 5000</p>
    </div>

    <div className="footer-column">
      <h4>Products</h4>
      <p>Beds</p>
      <p>Mattresses</p>
      <p>Headboards</p>
    </div>

    <div className="footer-column">
      <h4>Company</h4>
      <p>Terms & Conditions</p>
      <p>Privacy Policy</p>
      <p>Returns Policy</p>
    </div>

    <div className="footer-column">
      <h4>Follow Us</h4>
      <div className="social-icons">
        <FaFacebookF className="icon" />
        <FaInstagram className="icon" />
      </div>
    </div>

  </div>

  <div className="footer-bottom">
    © {new Date().getFullYear()} James Cresslawn Luxury Beds. All rights reserved.
  </div>
</footer>
    </div>
    );
}
export default HomePage;
