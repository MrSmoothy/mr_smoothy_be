-- Rename table from fruits to ingredients
-- This script renames the fruits table to ingredients and updates all foreign key references

-- Step 1: Drop all foreign key constraints that reference fruits table
-- Get constraint names dynamically and drop them

-- For predefined_drink_fruits table
SET @constraint_name = (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE 
  WHERE TABLE_SCHEMA = 'mr_smoothy' AND TABLE_NAME = 'predefined_drink_fruits' 
  AND REFERENCED_TABLE_NAME = 'fruits' LIMIT 1);
SET @sql = CONCAT('ALTER TABLE predefined_drink_fruits DROP FOREIGN KEY ', @constraint_name);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- For cart_item_fruits table
SET @constraint_name = (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE 
  WHERE TABLE_SCHEMA = 'mr_smoothy' AND TABLE_NAME = 'cart_item_fruits' 
  AND REFERENCED_TABLE_NAME = 'fruits' LIMIT 1);
SET @sql = CONCAT('ALTER TABLE cart_item_fruits DROP FOREIGN KEY ', @constraint_name);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- For order_item_fruits table
SET @constraint_name = (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE 
  WHERE TABLE_SCHEMA = 'mr_smoothy' AND TABLE_NAME = 'order_item_fruits' 
  AND REFERENCED_TABLE_NAME = 'fruits' LIMIT 1);
SET @sql = CONCAT('ALTER TABLE order_item_fruits DROP FOREIGN KEY ', @constraint_name);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Step 2: Rename columns from fruit_id to ingredient_id
ALTER TABLE predefined_drink_fruits CHANGE COLUMN fruit_id ingredient_id BIGINT NOT NULL;
ALTER TABLE cart_item_fruits CHANGE COLUMN fruit_id ingredient_id BIGINT NOT NULL;
ALTER TABLE order_item_fruits CHANGE COLUMN fruit_id ingredient_id BIGINT NOT NULL;

-- Step 3: Rename the main table
RENAME TABLE fruits TO ingredients;

-- Step 4: Re-add foreign key constraints
ALTER TABLE predefined_drink_fruits 
ADD CONSTRAINT fk_predefined_drink_fruit_ingredient 
FOREIGN KEY (ingredient_id) REFERENCES ingredients(id);

ALTER TABLE cart_item_fruits 
ADD CONSTRAINT fk_cart_item_fruit_ingredient 
FOREIGN KEY (ingredient_id) REFERENCES ingredients(id);

ALTER TABLE order_item_fruits 
ADD CONSTRAINT fk_order_item_fruit_ingredient 
FOREIGN KEY (ingredient_id) REFERENCES ingredients(id);

