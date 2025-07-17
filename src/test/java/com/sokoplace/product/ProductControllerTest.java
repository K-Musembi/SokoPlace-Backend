package com.sokoplace.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sokoplace.product.dto.ProductRequest;
import com.sokoplace.product.dto.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProductController.class)
@WithMockUser
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductRequest validProductRequest;
    private ProductRequest invalidProductRequest;
    private ProductResponse productResponse1;
    private ProductResponse productResponse2;

    @BeforeEach
    void setup() {
        validProductRequest = new ProductRequest("SKU001", "Electronics", "Samsung", "Galaxy S23", 999.99, "Latest Samsung phone");
        invalidProductRequest = new ProductRequest("", "Electronics", "Samsung", "Galaxy S23", 999.99, "Latest Samsung phone");
        productResponse1 = new ProductResponse(1L, "SKU001", "Electronics", "Samsung", "Galaxy S23", 999.99, "Latest Samsung phone", "/images/Electronics/Electronics.jpg");
        productResponse2 = new ProductResponse(2L, "SKU002", "Electronics", "Apple", "iPhone 15", 1099.99, "Latest Apple phone", "/images/Electronics/Electronics.jpg");
    }

    // --- GET /api/v1/products/{id} ---

    @Test
    @DisplayName("GET /api/v1/products/{id} - Should return product if found")
    void getProductById_whenProductExists_shouldReturnProduct() throws Exception {
        Long productId = 1L;
        given(productService.findProductById(productId)).willReturn(productResponse1);

        mockMvc.perform(get("/api/v1/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(productResponse1.Id()))
                .andExpect(jsonPath("$.sku").value(productResponse1.sku()))
                .andExpect(jsonPath("$.model").value(productResponse1.model()));

        verify(productService).findProductById(productId);
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} - Should return 404 if product not found")
    void getProductById_whenProductDoesNotExist_shouldReturnNotFound() throws Exception {
        Long productId = 99L;
        given(productService.findProductById(productId)).willThrow(new EntityNotFoundException("Product not found"));

        mockMvc.perform(get("/api/v1/products/{id}", productId))
                .andExpect(status().isNotFound());

        verify(productService).findProductById(productId);
    }

    // --- GET /api/v1/products/sku/{sku} ---

    @Test
    @DisplayName("GET /api/v1/products/sku/{sku} - Should return product if found")
    void getProductBySku_whenProductExists_shouldReturnProduct() throws Exception {
        String productSku = "SKU001";
        given(productService.findProductBySku(productSku)).willReturn(productResponse1);

        mockMvc.perform(get("/api/v1/products/sku/{sku}", productSku))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sku").value(productSku));

        verify(productService).findProductBySku(productSku);
    }

    // --- GET /api/v1/products/category/{category} ---

    @Test
    @DisplayName("GET /api/v1/products/category/{category} - Should return a list of products")
    void getProductByCategory_shouldReturnListOfProducts() throws Exception {
        String category = "Electronics";
        List<ProductResponse> products = List.of(productResponse1, productResponse2);
        given(productService.findProductByCategory(category)).willReturn(products);

        mockMvc.perform(get("/api/v1/products/category/{category}", category))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Galaxy S23"))
                .andExpect(jsonPath("$[1].name").value("iPhone 15"));

        verify(productService).findProductByCategory(category);
    }

    // --- GET /api/v1/products/brand/{category}/{brand} ---

    @Test
    @DisplayName("GET /api/v1/products/brand/{category}/{brand} - Should return a list of products")
    void getProductByBrand_shouldReturnListOfProducts() throws Exception {
        String category = "Electronics";
        String brand = "Samsung";
        List<ProductResponse> products = List.of(productResponse1);
        given(productService.findProductByBrand(category, brand)).willReturn(products);

        mockMvc.perform(get("/api/v1/products/brand/{category}/{brand}", category, brand))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].brand").value("Samsung"));

        verify(productService).findProductByBrand(category, brand);
    }

    // --- POST /api/v1/products ---

    @Test
    @DisplayName("POST /api/v1/products - Should create product with valid data")
    void createProduct_withValidRequest_shouldReturnCreated() throws Exception {
        given(productService.createProduct(any(ProductRequest.class))).willReturn(productResponse1);

        mockMvc.perform(post("/api/v1/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProductRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.sku").value("SKU001"));

        verify(productService).createProduct(any(ProductRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/products - Should return 400 Bad Request with invalid data")
    void createProduct_withInvalidRequest_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProductRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    // --- PUT /api/v1/products/{id} ---

    @Test
    @DisplayName("PUT /api/v1/products/{id} - Should update an existing product")
    void updateProduct_whenProductExists_shouldReturnUpdatedProduct() throws Exception {
        Long productId = 1L;
        given(productService.updateProduct(eq(productId), any(ProductRequest.class))).willReturn(productResponse1);

        mockMvc.perform(put("/api/v1/products/{id}", productId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProductRequest)))
                .andExpect(status().isCreated()) // Note: Controller returns 201, which is unconventional for PUT update
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Galaxy S23"));

        verify(productService).updateProduct(eq(productId), any(ProductRequest.class));
    }

    @Test
    @DisplayName("PUT /api/v1/products/{id} - Should return 404 if product does not exist")
    void updateProduct_whenProductDoesNotExist_shouldReturnNotFound() throws Exception {
        Long productId = 99L;
        given(productService.updateProduct(eq(productId), any(ProductRequest.class)))
                .willThrow(new EntityNotFoundException("Product not found"));

        mockMvc.perform(put("/api/v1/products/{id}", productId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProductRequest)))
                .andExpect(status().isNotFound());

        verify(productService).updateProduct(eq(productId), any(ProductRequest.class));
    }

    // --- DELETE /api/v1/products/{id} ---

    @Test
    @DisplayName("DELETE /api/v1/products/{id} - Should delete an existing product")
    void deleteProduct_whenProductExists_shouldReturnNoContent() throws Exception {
        Long productId = 1L;
        doNothing().when(productService).deleteProduct(productId);

        mockMvc.perform(delete("/api/v1/products/{id}", productId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(productId);
    }

    @Test
    @DisplayName("DELETE /api/v1/products/{id} - Should return 404 if product does not exist")
    void deleteProduct_whenProductDoesNotExist_shouldReturnNotFound() throws Exception {
        Long productId = 99L;
        doThrow(new EntityNotFoundException("Product not found")).when(productService).deleteProduct(productId);

        mockMvc.perform(delete("/api/v1/products/{id}", productId)
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(productService).deleteProduct(productId);
    }
}