package jamescresslawn.controller;


import jamescresslawn.entity.Product;
import jamescresslawn.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Basic product controller to verify the DB connection is working.
 * We will expand this with a full Service layer in the next phase.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")   // Allow your React frontend to call this API
public class ProductController {

    private final ProductRepository productRepository;

    // GET /api/products  →  returns all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    // GET /api/products/{id}  →  returns one product or 404
    @SuppressWarnings("null")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        return productRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/products/search?name=regal  →  search by name
    @GetMapping("/search")
    public ResponseEntity<List<Product>> search(@RequestParam String name) {
        return ResponseEntity.ok(
            productRepository.findByNameContainingIgnoreCase(name)
        );
    }

    // GET /api/products/filter?type=BED&size=QUEEN
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