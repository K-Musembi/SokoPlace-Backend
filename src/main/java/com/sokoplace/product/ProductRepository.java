package com.sokoplace.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByCategory(String category);
    Product findByBrand(String Category, String brand);
    Product findByModel(String Category, String brand, String model);
}

// JPA provides default methods for CRUD operations:
// save(S entity), saveAll(Iterable<S> entities)
// findById(ID id), existsById(ID id)
// findAll(), findAllById(Iterable<ID> ids)
// count()
// deleteById(ID id), delete(T entity), deleteAll(Iterable<? extends T> entities), deleteAll()
// flush()
// Paging and sorting methods
