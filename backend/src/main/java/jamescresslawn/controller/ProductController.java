package jamescresslawn.controller;


import jamescresslawn.entity.Product;
import jamescresslawn.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for product catalogue operations.
 * Exposes endpoints under /api/products.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductRepository productRepository;

    /**
     * Returns all products in the catalogue.
     *
     * @return list of all {@link Product} records
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    /**
     * Returns a single product by ID, or 404 if not found.
     *
     * @param id the product UUID
     * @return the matching product or a 404 response
     */
    @SuppressWarnings("null")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        return productRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Returns products whose names contain the given search term (case-insensitive).
     *
     * @param name the search term
     * @return matching products
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> search(@RequestParam String name) {
        return ResponseEntity.ok(
            productRepository.findByNameContainingIgnoreCase(name)
        );
    }

    /**
     * Returns products matching the given filter criteria.
     * All parameters are optional; omitted filters are ignored.
     *
     * @param type    product type (BED or MATTRESS)
     * @param size    bed/mattress size
     * @param comfort comfort level (mattresses only)
     * @return filtered product list
     */
    @GetMapping("/filter")
    public ResponseEntity<List<Product>> filter(
            @RequestParam(required = false) Product.ProductType type,
            @RequestParam(required = false) Product.ProductSize size,
            @RequestParam(required = false) Product.ComfortLevel comfort) {

        return ResponseEntity.ok(
            productRepository.findByFilters(type, size, comfort)
        );
    }
}