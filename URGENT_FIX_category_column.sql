-- URGENT FIX: Update category column size to support new category values
-- This MUST be run immediately to fix the "Data truncated" error
-- The longest category value is "ORGANIC_VEGETABLE" (18 characters)

-- Step 1: Update the category column size to VARCHAR(50) and default value
ALTER TABLE ingredients 
MODIFY COLUMN category VARCHAR(50) NOT NULL DEFAULT 'ORGANIC_FRUITS';

-- Step 2: Verify the column was updated correctly
SHOW COLUMNS FROM ingredients WHERE Field = 'category';

-- Step 3: Update existing records (if any) to map old categories to new ones
UPDATE ingredients SET category = 'ORGANIC_FRUITS' WHERE category = 'FRUIT';
UPDATE ingredients SET category = 'ORGANIC_VEGETABLE' WHERE category = 'VEGETABLE';
UPDATE ingredients SET category = 'BASE' WHERE category = 'ADDON';

-- Step 4: Verify the data
SELECT category, COUNT(*) as count FROM ingredients GROUP BY category;

