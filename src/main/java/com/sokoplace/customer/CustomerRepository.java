package com.sokoplace.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository  // Marks interface as a Spring Bean (good practice)
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // NB: JPA parses method names e.g. findByName and creates queries / implements

    Optional<Customer> findByName(String name);

    Optional<Customer> findByEmail(String name);

    // search for substring, ignore case
    List<Customer> findByNameContainingIgnoreCase(String name);

    // NB: JPA provides default methods for CRUD operations:
    // save(S entity), saveAll(Iterable<S> entities)
    // findById(ID id), existsById(ID id)
    // findAll(), findAllById(Iterable<ID> ids)
    // count()
    // deleteById(ID id), delete(T entity), deleteAll(Iterable<? extends T> entities), deleteAll()
    // flush()
    // Paging and sorting methods

}
