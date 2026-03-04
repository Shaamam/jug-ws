package tools.muthuishere.session1llmasapi.session2;

import org.springframework.ai.chat.model.ToolCallback;
import org.springframework.ai.chat.model.ToolCallbackDescription;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryTools {

    // ============================================================================
    // EXERCISE 2.1: Get all products
    // Refer to README.md for implementation
    // ============================================================================
    @ToolCallbackDescription("Get all products in the inventory")
    public List<Product> getProducts() {
        // TODO: Implement this method - see README Exercise 2.1
        return List.of();
    }

    // ============================================================================
    // EXERCISE 2.2: Get product by ID
    // Refer to README.md for implementation
    // ============================================================================
    @ToolCallbackDescription("Get a specific product by its ID")
    public Product getProductById(
            @ToolCallbackDescription("Product ID like P001, P002, P003") String productId) {
        // TODO: Implement this method - see README Exercise 2.2
        return null;
    }

    // ============================================================================
    // EXERCISE 2.3: Get low stock products
    // Refer to README.md for implementation
    // ============================================================================
    @ToolCallbackDescription("Get products that are low in stock (less than 10 items)")
    public List<Product> getLowStockProducts() {
        // TODO: Implement this method - see README Exercise 2.3
        return List.of();
    }
}
