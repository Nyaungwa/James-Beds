package jamescresslawn.config;

import jamescresslawn.entity.Product;
import jamescresslawn.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * DataSeeder runs automatically every time the app starts (CommandLineRunner).
 * It checks if products already exist, and only inserts if the table is empty.
 * This means you can restart the app without getting duplicate data.
 */
@Component           // Spring picks this up and manages it
@RequiredArgsConstructor  // Lombok: generates constructor for final fields (replaces @Autowired)
@Slf4j               // Lombok: gives us a log.info() / log.debug() logger
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    @SuppressWarnings("null")
    @Override
    public void run(String... args) {
        // Only seed if there are no products yet
        if (productRepository.count() > 0) {
            log.info("Products already seeded. Skipping.");
            return;
        }

        log.info("Seeding products into database...");

        List<Product> products = List.of(

            // ---- BEDS ----
            Product.builder()
                .name("Regal Sleigh Bed - Queen")
                .description("Hand-crafted solid mahogany sleigh bed with a timeless design. " +
                             "Features a high curved headboard and footboard for a classic luxury feel.")
                .type(Product.ProductType.BED)
                .size(Product.ProductSize.QUEEN)
                .price(new BigDecimal("24999.00"))
                .discountPrice(new BigDecimal("21499.00"))
                .imageUrl("https://placehold.co/800x600?text=Regal+Sleigh+Bed")
                .inStock(true)
                .build(),

            Product.builder()
                .name("Imperial Platform Bed - King")
                .description("Ultra-low profile king platform bed in rich walnut finish. " +
                             "Integrated LED lighting underneath creates a floating effect.")
                .type(Product.ProductType.BED)
                .size(Product.ProductSize.KING)
                .price(new BigDecimal("32500.00"))
                .imageUrl("https://placehold.co/800x600?text=Imperial+Platform+Bed")
                .inStock(true)
                .build(),

            Product.builder()
                .name("Heritage Four-Poster Bed - Queen")
                .description("Solid oak four-poster bed with hand-turned posts. " +
                             "A statement piece for any master bedroom.")
                .type(Product.ProductType.BED)
                .size(Product.ProductSize.QUEEN)
                .price(new BigDecimal("38000.00"))
                .discountPrice(new BigDecimal("33500.00"))
                .imageUrl("https://placehold.co/800x600?text=Heritage+Four-Poster")
                .inStock(true)
                .build(),

            Product.builder()
                .name("Velvet Upholstered Bed - Double")
                .description("Deep charcoal velvet upholstered bed frame with diamond-tufted " +
                             "headboard. Luxurious and contemporary.")
                .type(Product.ProductType.BED)
                .size(Product.ProductSize.DOUBLE)
                .price(new BigDecimal("15999.00"))
                .imageUrl("https://placehold.co/800x600?text=Velvet+Upholstered+Bed")
                .inStock(true)
                .build(),

            // ---- MATTRESSES ----
            Product.builder()
                .name("Cresslawn Signature Pocket Spring - Queen")
                .description("2000 individual pocket springs with a pillow-top layer of " +
                             "natural wool and memory foam. Our flagship mattress.")
                .type(Product.ProductType.MATTRESS)
                .size(Product.ProductSize.QUEEN)
                .comfortLevel(Product.ComfortLevel.MEDIUM)
                .price(new BigDecimal("18500.00"))
                .discountPrice(new BigDecimal("15999.00"))
                .imageUrl("https://placehold.co/800x600?text=Signature+Pocket+Spring")
                .inStock(true)
                .build(),

            Product.builder()
                .name("Orthopedic Firm Support - King")
                .description("High-density orthopedic foam with a firm feel for back support. " +
                             "Recommended for those with back pain.")
                .type(Product.ProductType.MATTRESS)
                .size(Product.ProductSize.KING)
                .comfortLevel(Product.ComfortLevel.FIRM)
                .price(new BigDecimal("21000.00"))
                .imageUrl("https://placehold.co/800x600?text=Orthopedic+Firm")
                .inStock(true)
                .build(),

            Product.builder()
                .name("Cloud Plush Memory Foam - Queen")
                .description("Sink into 5 layers of premium memory foam. " +
                             "Temperature-regulating gel layer keeps you cool all night.")
                .type(Product.ProductType.MATTRESS)
                .size(Product.ProductSize.QUEEN)
                .comfortLevel(Product.ComfortLevel.SOFT)
                .price(new BigDecimal("16800.00"))
                .imageUrl("https://placehold.co/800x600?text=Cloud+Plush+Memory+Foam")
                .inStock(true)
                .build(),

            Product.builder()
                .name("Junior Comfort Mattress - Single")
                .description("Supportive children's mattress with a breathable cover. " +
                             "Medium firm feel designed for growing bodies.")
                .type(Product.ProductType.MATTRESS)
                .size(Product.ProductSize.SINGLE)
                .comfortLevel(Product.ComfortLevel.MEDIUM)
                .price(new BigDecimal("4200.00"))
                .discountPrice(new BigDecimal("3599.00"))
                .imageUrl("https://placehold.co/800x600?text=Junior+Comfort+Mattress")
                .inStock(true)
                .build()
        );

        productRepository.saveAll(products);
        log.info("Successfully seeded {} products.", products.size());
    }
}
