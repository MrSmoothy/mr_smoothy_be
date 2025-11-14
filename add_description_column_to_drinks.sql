-- Add description column to predefined_drinks table
-- This script adds the description column to the existing predefined_drinks table

ALTER TABLE predefined_drinks 
ADD COLUMN description TEXT NULL;

-- Optional: Update existing records with a default description if needed
-- UPDATE predefined_drinks SET description = CONCAT('คำอธิบายสำหรับ ', name) WHERE description IS NULL;

