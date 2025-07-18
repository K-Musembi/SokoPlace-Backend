package com.sokoplace.test;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DatabaseIntegrationTest {
    @Container
    // This container will be started once and shared across all tests that extend this class.
    static final PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
        // Let Hibernate create the schema for our test container
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        // Disable Flyway for this test slice
        registry.add("spring.flyway.enabled", () -> "false");
        // This property was needed for OrderRepositoryTest, it's safe to have it for all.
        registry.add("spring.jpa.properties.hibernate.globally_quoted_identifiers", () -> "true");
    }
}
