package com.sokoplace.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    //Spring Data JPA reads the method names in your repository interface and automatically
    // generates the corresponding database query based on those names and parameters.

    List<Product> findByCategory(String category);
    List<Product> findByCategoryAndBrand(String category, String brand);
    // Be explicit! Above method if written as 'findByBrand' will cause an error
    // JPA parses the method name to create SQL query
    List<Product> findByCategoryAndBrandAndModel(String category, String brand, String model);

    Product findBySku(String sku);
}

// JPA provides default methods for CRUD operations:
// save(S entity), saveAll(Iterable<S> entities)
// findById(ID id), existsById(ID id)
// findAll(), findAllById(Iterable<ID> ids)
// count()
// deleteById(ID id), delete(T entity), deleteAll(Iterable<? extends T> entities), deleteAll()
// flush()
// Paging and sorting methods
