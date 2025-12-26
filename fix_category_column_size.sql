-- Fix category column size to support new category values
-- This script ensures the category column is wide enough for values like "ORGANIC_FRUITS", "ORGANIC_VEGETABLE", etc.
-- The longest category value is "ORGANIC_VEGETABLE" (18 characters), so VARCHAR(50) is safe

-- Step 1: Update the category column size and default value for ingredients table
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
SELECT category, COUNT(*) as count FROM ingredients GROUP BY category;

-- ============================================
-- If your table is still named 'fruits' instead of 'ingredients', use this version:
-- ============================================
/*
ALTER TABLE fruits 
MODIFY COLUMN category VARCHAR(50) NOT NULL DEFAULT 'ORGANIC_FRUITS';

UPDATE fruits SET category = 'ORGANIC_FRUITS' WHERE category = 'FRUIT';
UPDATE fruits SET category = 'ORGANIC_VEGETABLE' WHERE category = 'VEGETABLE';
UPDATE fruits SET category = 'BASE' WHERE category = 'ADDON';

SELECT category, COUNT(*) as count FROM fruits GROUP BY category;
*/

