-- Rename the "order" table to "customer_order" to avoid SQL keyword conflicts and improve clarity.
ALTER TABLE "order" RENAME TO customer_order;

-- When using SERIAL/BIGSERIAL, PostgreSQL creates a sequence for the primary key.
-- This sequence is not automatically renamed with the table, so we rename it manually for consistency.
ALTER SEQUENCE order_id_seq RENAME TO customer_order_id_seq;

-- Rename the foreign key column in the join table to match the new entity mapping.
ALTER TABLE order_item RENAME COLUMN order_id TO customer_order_id;

-- Rename the index on the customer_id foreign key to match the new table name.
ALTER INDEX idx_order_customer_id RENAME TO idx_customer_order_customer_id;

-- Note: Foreign key constraints in other tables (like 'fk_order' in 'order_item')
-- that reference the renamed table are automatically updated by PostgreSQL,
-- so they do not need to be manually altered.