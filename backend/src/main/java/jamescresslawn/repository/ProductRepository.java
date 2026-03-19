package jamescresslawn.repository;

import jamescresslawn.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    // Find all products by type (BED or MATTRESS)
    List<Product> findByType(Product.ProductType type);

    // Find all products by size
    List<Product> findBySize(Product.ProductSize size);

    // Find in-stock products only
    List<Product> findByInStockTrue();

    // Search by name (case-insensitive, partial match)
    // %name% means "contains this text anywhere"
    List<Product> findByNameContainingIgnoreCase(String name);

    // Custom JPQL query for filtering by multiple criteria
    // :type etc. are named parameters you pass in when calling this method
    @Query("SELECT p FROM Product p WHERE " +
           "(:type IS NULL OR p.type = :type) AND " +
           "(:size IS NULL OR p.size = :size) AND " +
           "(:comfort IS NULL OR p.comfortLevel = :comfort) AND " +
           "p.inStock = true")
    List<Product> findByFilters(
        @org.springframework.data.repository.query.Param("type") Product.ProductType type,
        @org.springframework.data.repository.query.Param("size") Product.ProductSize size,
        @org.springframework.data.repository.query.Param("comfort") Product.ComfortLevel comfort
    );
}

