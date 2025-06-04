-- Create Customer Table
CREATE TABLE customer (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(50) NOT NULL,
                          email VARCHAR(50) NOT NULL UNIQUE, -- Added UNIQUE constraint for email
                          created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                          updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

-- Create Product Table
CREATE TABLE product (
                         id BIGSERIAL PRIMARY KEY,
                         sku VARCHAR(20) NOT NULL UNIQUE, -- Added UNIQUE constraint for sku
                         category VARCHAR(50) NOT NULL,
                         brand VARCHAR(50) NOT NULL,
                         model VARCHAR(50) NOT NULL,
                         price DOUBLE PRECISION NOT NULL,
                         description VARCHAR(255),
                         image_url VARCHAR(255),
                         created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                         updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

-- Create Order Table
CREATE TABLE "order" ( -- Quoted "order" as it's a reserved keyword in SQL
                         id BIGSERIAL PRIMARY KEY,
                         customer_id BIGINT NOT NULL,
                         created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                         updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                         CONSTRAINT fk_customer
                             FOREIGN KEY(customer_id)
                                 REFERENCES customer(id)
                                 ON DELETE CASCADE -- Reflects orphanRemoval=true and CascadeType.ALL for customer removal
);

-- Create Order_Item Join Table (for ManyToMany relationship between Order and Product)
CREATE TABLE order_item (
                            order_id BIGINT NOT NULL,
                            product_id BIGINT NOT NULL,
                            PRIMARY KEY (order_id, product_id),
                            CONSTRAINT fk_order
                                FOREIGN KEY(order_id)
                                    REFERENCES "order"(id)
                                    ON DELETE CASCADE,
                            CONSTRAINT fk_product
                                FOREIGN KEY(product_id)
                                    REFERENCES product(id)
                                    ON DELETE CASCADE
);

-- Optional: Add indexes for foreign keys and frequently queried columns for performance
CREATE INDEX idx_order_customer_id ON "order"(customer_id);
CREATE INDEX idx_order_item_product_id ON order_item(product_id);
CREATE INDEX idx_product_category ON product(category);
CREATE INDEX idx_product_brand ON product(brand);
