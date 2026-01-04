-- เพิ่มคอลัมน์ popular ในตาราง predefined_drinks
-- สำหรับตั้งค่าเมนูที่ต้องการแสดงในหน้า home popular menu

ALTER TABLE predefined_drinks
ADD COLUMN popular BOOLEAN NOT NULL DEFAULT FALSE AFTER active;

-- อัพเดตเมนูที่มีอยู่แล้วให้ popular = false (default)
-- Admin สามารถตั้งค่าเป็น popular ได้ผ่าน API endpoint

COMMENT ON COLUMN predefined_drinks.popular IS 'ตั้งค่าเมนูที่เป็น popular สำหรับแสดงในหน้า home';
