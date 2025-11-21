-- Add phone_number and date_of_birth columns to users table
ALTER TABLE users
ADD COLUMN phone_number VARCHAR(20) NULL,
ADD COLUMN date_of_birth DATE NULL;

