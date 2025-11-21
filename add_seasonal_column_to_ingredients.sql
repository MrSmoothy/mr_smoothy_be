-- Add seasonal column to ingredients table
-- This script adds the seasonal column to the existing ingredients table

ALTER TABLE ingredients
ADD COLUMN seasonal BOOLEAN NOT NULL DEFAULT FALSE;

-- Optional: Update existing records to mark some as seasonal if needed
-- UPDATE ingredients SET seasonal = TRUE WHERE name IN ('Strawberry', 'Mango', 'Watermelon');

