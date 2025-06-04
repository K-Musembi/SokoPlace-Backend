package com.sokoplace.product;

//  Though you could inject repositories directly into controllers,
//  it is best practice to have a service layer.

import com.sokoplace.product.dto.ProductRequest;
import com.sokoplace.product.dto.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        if (productRepository.findByCategoryAndBrandAndModel(
                productRequest.category(),
                productRequest.brand(),
                productRequest.model()) != null) {
            throw new DataIntegrityViolationException("Product already exists");
        }
        Product product = new Product();
        Product createdProduct = getProduct(product, productRequest);
        productRepository.save(createdProduct);
        return mapToProductResponse(createdProduct);
    }

    @Transactional
    public ProductResponse findProductById(Long Id) {
        Product product = productRepository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return mapToProductResponse(product);
    }

    @Transactional
    public List<ProductResponse> findProductByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category);

        return products.stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    @Transactional
    public List<ProductResponse> findProductByBrand(String category, String brand) {
        List<Product> products = productRepository.findByCategoryAndBrand(category, brand);
        return products.stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    @Transactional
    public ProductResponse findProductBySku(String sku) {
        Product product = productRepository.findBySku(sku);
        return mapToProductResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long Id, ProductRequest productRequest) {
        Product product = productRepository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        Product updatedProduct = getProduct(product, productRequest);
        productRepository.save(updatedProduct);
        return mapToProductResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long Id) {
        Product product = productRepository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        productRepository.delete(product);
    }

    // Similar lines repeated for createProduct and updateProduct. Common method created
    private Product getProduct(Product product, ProductRequest productRequest) {
        product.setSku(productRequest.sku());
        product.setCategory(productRequest.category());
        product.setBrand(productRequest.brand());
        product.setModel(productRequest.model());  // setter method, generated through @Data (Lombok) in model file
        product.setPrice(productRequest.price());
        product.setDescription(productRequest.description());
        product.setImageUrl("resources/images/" + productRequest.category() + "/"
                + productRequest.brand() + "/" + productRequest.model() + ".jpg");
        return product;
    }

    private ProductResponse mapToProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getCategory(),  // getter method
                product.getBrand(),
                product.getModel(),
                product.getPrice(),
                product.getDescription(),
                product.getImageUrl()
        );
    }

}
