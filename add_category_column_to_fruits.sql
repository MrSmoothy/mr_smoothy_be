-- Add category column to fruits table
-- This script adds the category column to the existing fruits table

ALTER TABLE fruits 
ADD COLUMN category VARCHAR(50) NOT NULL DEFAULT 'FRUIT';

-- Update existing records to have FRUIT category by default
UPDATE fruits SET category = 'FRUIT' WHERE category IS NULL OR category = '';


-- Optional: Set some example categories if needed
-- UPDATE fruits SET category = 'VEGETABLE' WHERE name LIKE '%ผัก%' OR name LIKE '%Vegetable%';
-- UPDATE fruits SET category = 'ADDON' WHERE name LIKE '%โยเกิร์ต%' OR name LIKE '%น้ำผึ้ง%' OR name LIKE '%Yogurt%' OR name LIKE '%Honey%';


