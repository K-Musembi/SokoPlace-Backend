package com.sokoplace.product;

import com.sokoplace.test.DatabaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ProductRepositoryTest extends DatabaseIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    private Product product1;
    // private Product product2;

    @BeforeEach
    void setUp() {
        // This setup acts as a common "Arrange" step for all tests
        productRepository.deleteAll();

        product1 = new Product(null, "SKU001", "Electronics", "Samsung", "Galaxy S23", 999.99, "Latest Samsung smartphone", "https://example.com/s23.jpg", new ArrayList<>(), null, null);
        Product product2 = new Product(null, "SKU002", "Electronics", "Apple", "iPhone 15", 1099.99, "Latest Apple smartphone", "https://example.com/iphone15.jpg", new ArrayList<>(), null, null);

        // Persist the entities to get their managed state (with IDs) for tests
        product1 = productRepository.save(product1);
        productRepository.save(product2);
    }

    @Test
    @DisplayName("Should save a new product successfully")
    void saveProduct() {
        // Arrange
        Product newProduct = new Product(null, "SKU003", "Laptops", "Dell", "XPS 15", 1500.00, "A powerful laptop", "https://example.com/xps15.jpg", new ArrayList<>(), null, null);

        // Act
        Product savedProduct = productRepository.save(newProduct);

        // Assert
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getSku()).isEqualTo("SKU003");
        assertThat(savedProduct.getCreatedAt()).isNotNull();
        assertThat(savedProduct.getUpdatedAt()).isNotNull();
        assertThat(savedProduct.getCreatedAt()).isEqualTo(savedProduct.getUpdatedAt());

        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        assertThat(foundProduct).hasValueSatisfying(p -> assertThat(p.getBrand()).isEqualTo("Dell"));
    }

    @Test
    @DisplayName("Should find a product by its ID when it exists")
    void findProductById_whenExists() {
        // Arrange (data is pre-arranged in setUp)
        Long existingProductId = product1.getId();

        // Act
        Optional<Product> foundProductOpt = productRepository.findById(existingProductId);

        // Assert - Using hasValueSatisfying for a more fluent and safe assertion on the Optional.
        assertThat(foundProductOpt).hasValueSatisfying(foundProduct -> {
            assertThat(foundProduct.getSku()).isEqualTo(product1.getSku());
            assertThat(foundProduct.getBrand()).isEqualTo(product1.getBrand());
        });
    }

    @Test
    @DisplayName("Should return empty optional when finding by an ID that does not exist")
    void findProductById_whenNotExists() {
        // Arrange
        long nonExistentId = 999L;

        // Act
        Optional<Product> foundProduct = productRepository.findById(nonExistentId);

        // Assert
        assertThat(foundProduct).isEmpty();
    }

    @Test
    @DisplayName("Should return all products when findAll is called")
    void findAllProducts() {
        // Arrange (data is pre-arranged in setUp, expecting 2 products)

        // Act
        List<Product> products = productRepository.findAll();

        // Assert - Using extracting() for more specific and readable assertions on collections
        assertThat(products)
                .isNotNull()
                .hasSize(2)
                .extracting(Product::getSku)
                .containsExactlyInAnyOrder("SKU001", "SKU002");
    }

    @Test
    @DisplayName("Should update an existing product")
    void updateProduct() {
        // Arrange
        Product productToUpdate = productRepository.findById(product1.getId()).orElseThrow();
        // var originalUpdateTime = productToUpdate.getUpdatedAt();

        // Modify the entity
        productToUpdate.setPrice(899.99);
        productToUpdate.setDescription("Discounted price!");

        // Act
        Product updatedProduct = productRepository.save(productToUpdate);

        // Assert
        assertThat(updatedProduct.getPrice()).isEqualTo(899.99);
        assertThat(updatedProduct.getDescription()).isEqualTo("Discounted price!");
        assertThat(updatedProduct.getUpdatedAt()).isNotNull();
        // assertThat(updatedProduct.getUpdatedAt()).isAfter(originalUpdateTime);

        Optional<Product> freshlyFetchedProduct = productRepository.findById(product1.getId());
        assertThat(freshlyFetchedProduct).hasValueSatisfying(p -> assertThat(p.getPrice()).isEqualTo(899.99));
    }

    @Test
    @DisplayName("Should delete a product by its ID")
    void deleteProduct() {
        // Arrange
        Long product1Id = product1.getId();
        // Verify pre-condition
        assertThat(productRepository.existsById(product1Id)).isTrue();
        long initialCount = productRepository.count();

        // Act
        productRepository.deleteById(product1Id);

        // Assert
        assertThat(productRepository.existsById(product1Id)).isFalse();
        assertThat(productRepository.count()).isEqualTo(initialCount - 1);
    }

    @Test
    @DisplayName("Should fail to save product with null SKU")
    void saveProduct_withNullSku_shouldFail() {
        // Arrange
        Product invalidProduct = new Product(null, null, "Category", "Brand", "Model", 100.0, null, null, new ArrayList<>(), null, null);

        // Act & Assert - The 'act' is saving the invalid product, and we 'assert' that it throws the correct exception.
        assertThrows(DataIntegrityViolationException.class, () -> productRepository.saveAndFlush(invalidProduct));
    }
}