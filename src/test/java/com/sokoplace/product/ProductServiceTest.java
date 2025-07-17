package com.sokoplace.product;

import com.sokoplace.product.dto.ProductRequest;
import com.sokoplace.product.dto.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;
    private ProductRequest productRequest;

    @BeforeEach
    void setup() {
        productRequest = new ProductRequest("SKU001", "Electronics", "Samsung", "Galaxy S23", 999.99, "Latest Samsung smartphone");
        product1 = new Product(1L, "SKU001", "Electronics", "Samsung", "Galaxy S23", 999.99, "Latest Samsung smartphone", "/images/Electronics/Electronics.jpg", null, null, null);
        product2 = new Product(2L, "SKU002", "Electronics", "Apple", "iPhone 15", 1099.99, "Latest Apple smartphone", "/images/Electronics/Electronics.jpg", null, null, null);
    }

    @Test
    @DisplayName("Should create and return a new product")
    void shouldCreateProduct() {
        // Given
        // Product does not already exist
        given(productRepository.findByCategoryAndBrandAndModel(
                productRequest.category(), productRequest.brand(), productRequest.model()))
                .willReturn(null);

        // Mock the save operation to return the product with an ID
        given(productRepository.save(any(Product.class))).willReturn(product1);

        // When
        ProductResponse createdProduct = productService.createProduct(productRequest);

        // Then
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.Id()).isEqualTo(1L);
        assertThat(createdProduct.sku()).isEqualTo(productRequest.sku());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw DataIntegrityViolationException when creating a product that already exists")
    void shouldThrowExceptionWhenProductExists() {
        // Given
        given(productRepository.findByCategoryAndBrandAndModel(
                productRequest.category(), productRequest.brand(), productRequest.model()))
                .willReturn(Optional.ofNullable(product1));

        // When & Then
        assertThatThrownBy(() -> productService.createProduct(productRequest))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Product already exists");

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should find product by ID when it exists")
    void shouldFindProductById() {
        // Given
        given(productRepository.findById(1L)).willReturn(Optional.of(product1));

        // When
        ProductResponse foundProduct = productService.findProductById(1L);

        // Then
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.Id()).isEqualTo(product1.getId());
        assertThat(foundProduct.model()).isEqualTo(product1.getModel());
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when product ID does not exist")
    void shouldThrowExceptionWhenProductIdNotFound() {
        // Given
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.findProductById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Product not found");
        verify(productRepository).findById(99L);
    }

    @Test
    @DisplayName("Should find all products for a given category")
    void shouldFindProductsByCategory() {
        // Given
        given(productRepository.findByCategory("Electronics")).willReturn(Arrays.asList(product1, product2));

        // When
        List<ProductResponse> products = productService.findProductByCategory("Electronics");

        // Then
        assertThat(products).isNotNull();
        assertThat(products).hasSize(2);
        verify(productRepository).findByCategory("Electronics");
    }

    @Test
    @DisplayName("Should find all products for a given category and brand")
    void shouldFindProductsByCategoryAndBrand() {
        // Given
        given(productRepository.findByCategoryAndBrand("Electronics", "Samsung")).willReturn(List.of(product1));

        // When
        List<ProductResponse> products = productService.findProductByBrand("Electronics", "Samsung");

        // Then
        assertThat(products).isNotNull();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).brand()).isEqualTo("Samsung");
        verify(productRepository).findByCategoryAndBrand("Electronics", "Samsung");
    }

    @Test
    @DisplayName("Should find product by SKU when it exists")
    void shouldFindProductBySku() {
        // Given
        given(productRepository.findBySku("SKU001")).willReturn(product1);

        // When
        ProductResponse foundProduct = productService.findProductBySku("SKU001");

        // Then
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.sku()).isEqualTo("SKU001");
        verify(productRepository).findBySku("SKU001");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when SKU does not exist")
    void shouldThrowExceptionWhenSkuNotFound() {
        // Given
        given(productRepository.findBySku("SKU999")).willReturn(null);

        // When & Then
        assertThatThrownBy(() -> productService.findProductBySku("SKU999"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Product not found");
        verify(productRepository).findBySku("SKU999");
    }

    @Test
    @DisplayName("Should update product when found")
    void shouldUpdateProduct() {
        // Given
        Long productId = 1L;
        ProductRequest updateRequest = new ProductRequest("SKU001-U", "Electronics", "Samsung", "Galaxy S23 Ultra", 1199.99, "Updated description");

        Product updatedEntity = new Product(productId, "SKU001-U", "Electronics", "Samsung", "Galaxy S23 Ultra", 1199.99, "Updated description", "/images/Electronics/Electronics.jpg", null, null, null);

        given(productRepository.findById(productId)).willReturn(Optional.of(product1));
        given(productRepository.save(any(Product.class))).willReturn(updatedEntity);

        // When
        ProductResponse updatedProduct = productService.updateProduct(productId, updateRequest);

        // Then
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.price()).isEqualTo(1199.99);
        assertThat(updatedProduct.model()).isEqualTo("Galaxy S23 Ultra");
        assertThat(updatedProduct.Id()).isEqualTo(productId);

        // Capture the argument passed to save to verify its contents
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        Product savedProduct = productCaptor.getValue();

        assertThat(savedProduct.getDescription()).isEqualTo("Updated description");
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should delete product when found")
    void shouldDeleteProduct() {
        // Given
        Long productId = 1L;
        given(productRepository.findById(productId)).willReturn(Optional.of(product1));
        willDoNothing().given(productRepository).delete(product1);

        // When
        productService.deleteProduct(productId);

        // Then
        verify(productRepository).findById(productId);
        verify(productRepository).delete(product1);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when trying to delete a non-existent product")
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        // Given
        Long productId = 99L;
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.deleteProduct(productId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Product not found");

        verify(productRepository, never()).delete(any(Product.class));
    }
}