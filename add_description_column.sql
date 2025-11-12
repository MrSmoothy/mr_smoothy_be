-- Add description column to fruits table
-- This script adds the description column to the existing fruits table

ALTER TABLE fruits 
ADD COLUMN description TEXT NULL;

-- Optional: Update existing records with a default description if needed
-- UPDATE fruits SET description = CONCAT('คำอธิบายสำหรับ ', name) WHERE description IS NULL;

