package com.sokoplace.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// JPA parses method names e.g. findByName and creates / implements SQL queries
// Optional used to handle cases where the object / Entity isn't found
// Optional methods include: isPresent(), ifPresent(), orElseThrow(), etc.
// @Repository: Marks interface as a Spring Bean (good practice)

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Custom abstract methods

    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);
}


// JPA provides default methods for CRUD operations:
// save(S entity), saveAll(Iterable<S> entities)
// findById(ID id), existsById(ID id)
// findAll(), findAllById(Iterable<ID> ids)
// count()
// deleteById(ID id), delete(T entity), deleteAll(Iterable<? extends T> entities), deleteAll()
// flush()
// Paging and sorting methods
