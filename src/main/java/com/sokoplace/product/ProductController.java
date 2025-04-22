package com.sokoplace.product;

import com.sokoplace.product.dto.ProductRequest;
import com.sokoplace.product.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ResponseEntity represents the HTTP response object: status code, headers, body (payload)
// ResponseEntity.ok(createdProduct) -> 200 Ok, ResponseEntity.status(HttpStatus.CREATED).body(savedProduct) -> 201 created
// ResponseEntity.notFound(), ResponseEntity.badRequest().body("Invalid"), etc.
// Create a custom header 'customHeaders' object, then ResponseEntity.ok().headers(customHeaders).body(...)
// @Valid: corresponds to validation in DTO; for incoming @ResponseBody

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse responseObject = productService.findProductById(id);
        return ResponseEntity.ok(responseObject);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProductByCategory(@RequestParam String category) {
        List<ProductResponse> responseObject = productService.findProductByCategory(category);
        return ResponseEntity.ok(responseObject);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProductByBrand(
            @RequestParam String category,
            @RequestParam String brand) {
        List<ProductResponse> responseObject = productService.findProductByBrand(category, brand);
        return ResponseEntity.ok(responseObject);
    }

    @GetMapping
    public ResponseEntity<ProductResponse> getProductByModel(
            @RequestParam String category,
            @RequestParam String brand,
            @RequestParam String model
    ) {
        ProductResponse responseObject = productService.findProductByModel(category, brand, model);
        return ResponseEntity.ok(responseObject);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {
        ProductResponse responseObject = productService.createProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseObject);  // successfully created is 201, ok is 200
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequest productRequest) {
        ProductResponse responseObject = productService.updateProduct(id, productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseObject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponse> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}


