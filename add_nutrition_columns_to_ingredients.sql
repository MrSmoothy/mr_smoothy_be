-- Add nutrition and AI-generated fields to ingredients table
-- This script adds columns for USDA nutrition data and OpenAI-generated flavor/pairing information

ALTER TABLE ingredients
ADD COLUMN calorie DECIMAL(10, 2) NULL COMMENT 'Calories per 100g',
ADD COLUMN protein DECIMAL(10, 2) NULL COMMENT 'Protein in grams per 100g',
ADD COLUMN fiber DECIMAL(10, 2) NULL COMMENT 'Fiber in grams per 100g',
ADD COLUMN vitamins JSON NULL COMMENT 'Vitamins data as JSON object',
ADD COLUMN minerals JSON NULL COMMENT 'Minerals data as JSON object',
ADD COLUMN flavor_profile VARCHAR(255) NULL COMMENT 'Flavor profile e.g., sweet, tropical',
ADD COLUMN taste_notes TEXT NULL COMMENT 'Detailed taste description',
ADD COLUMN best_mix_pairing JSON NULL COMMENT 'Best pairing ingredients as JSON array',
ADD COLUMN avoid_pairing JSON NULL COMMENT 'Ingredients to avoid pairing as JSON array',
ADD COLUMN raw_usda_data LONGTEXT NULL COMMENT 'Complete USDA API response as JSON string';

