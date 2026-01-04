-- Add category column to predefined_drinks table
-- This script adds the category column to the existing predefined_drinks table
-- Following OOP principles, the category is an enum type stored as VARCHAR

ALTER TABLE predefined_drinks 
ADD COLUMN category VARCHAR(50) NOT NULL DEFAULT 'OTHER';

-- Update existing records to have OTHER category by default
UPDATE predefined_drinks SET category = 'OTHER' WHERE category IS NULL OR category = '';

-- Verify the update
-- SELECT category, COUNT(*) as count FROM predefined_drinks GROUP BY category;

