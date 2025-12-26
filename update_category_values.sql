-- Update category column to support new category values
-- This script updates the category column to use the new category enum values
-- Run this script after updating the Java enum to ensure database compatibility

-- Step 1: Update the default value for the category column in ingredients table
-- (If table is still named 'fruits', run the fruits version instead)
ALTER TABLE ingredients 
MODIFY COLUMN category VARCHAR(50) NOT NULL DEFAULT 'ORGANIC_FRUITS';

-- Step 2: Update existing records to map old categories to new ones
-- Map FRUIT -> ORGANIC_FRUITS
UPDATE ingredients SET category = 'ORGANIC_FRUITS' WHERE category = 'FRUIT';

-- Map VEGETABLE -> ORGANIC_VEGETABLE  
UPDATE ingredients SET category = 'ORGANIC_VEGETABLE' WHERE category = 'VEGETABLE';

-- Map ADDON -> BASE (or you can manually categorize these later)
-- Note: You may want to review ADDON items individually to categorize them properly
-- as BASE, PROTEIN, TOPPING, or SWEETENER
UPDATE ingredients SET category = 'BASE' WHERE category = 'ADDON';

-- Step 3: Verify the update
-- SELECT category, COUNT(*) as count FROM ingredients GROUP BY category;

-- If your table is still named 'fruits' instead of 'ingredients', use this version:
/*
ALTER TABLE fruits 
MODIFY COLUMN category VARCHAR(50) NOT NULL DEFAULT 'ORGANIC_FRUITS';

UPDATE fruits SET category = 'ORGANIC_FRUITS' WHERE category = 'FRUIT';
UPDATE fruits SET category = 'ORGANIC_VEGETABLE' WHERE category = 'VEGETABLE';
UPDATE fruits SET category = 'BASE' WHERE category = 'ADDON';
*/
