-- Add customer_name and customer_email columns to orders table for guest orders
ALTER TABLE orders
ADD COLUMN customer_name VARCHAR(255) NULL,
ADD COLUMN customer_email VARCHAR(255) NULL;

-- Make user_id nullable to support guest orders
ALTER TABLE orders
MODIFY COLUMN user_id BIGINT NULL;

