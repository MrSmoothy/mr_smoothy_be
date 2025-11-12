-- Script สำหรับอัปเดต URL ของรูปภาพผลไม้ใน database
-- ให้แน่ใจว่า URL มี extension (.png, .jpg, etc.)

-- ดูข้อมูลปัจจุบันก่อนอัปเดต
SELECT id, name, image_url FROM fruits WHERE image_url IS NOT NULL;

-- อัปเดต URL ที่ไม่มี extension ให้เพิ่ม .png
-- ถ้า URL เป็น "fruit/banana" หรือ "/fruit/banana" 
-- ให้แก้เป็น "http://localhost:9000/fruit/banana.png"
UPDATE fruits 
SET image_url = CASE 
    -- ถ้า URL เริ่มต้นด้วย http:// หรือ https:// แล้ว ไม่ต้องแก้
    WHEN image_url LIKE 'http://%' OR image_url LIKE 'https://%' THEN image_url
    
    -- ถ้า URL เป็น "fruit/banana" (ไม่มี http:// และไม่มี .png)
    WHEN image_url LIKE 'fruit/%' AND image_url NOT LIKE '%.%' 
    THEN CONCAT('http://localhost:9000/', image_url, '.png')
    
    -- ถ้า URL เป็น "/fruit/banana"
    WHEN image_url LIKE '/fruit/%' AND image_url NOT LIKE '%.%'
    THEN CONCAT('http://localhost:9000', image_url, '.png')
    
    -- ถ้า URL เป็น "banana" อย่างเดียว
    WHEN image_url NOT LIKE '%/%' AND image_url NOT LIKE '%.%'
    THEN CONCAT('http://localhost:9000/fruit/', image_url, '.png')
    
    -- ถ้ามี / แต่ไม่มี extension
    WHEN image_url NOT LIKE '%.%' 
    THEN CONCAT('http://localhost:9000/', image_url, '.png')
    
    -- ถ้าไม่มี http:// แต่มี extension แล้ว
    WHEN image_url NOT LIKE 'http://%' AND image_url NOT LIKE 'https://%'
    THEN CONCAT('http://localhost:9000/', 
        CASE WHEN image_url LIKE '/%' THEN SUBSTRING(image_url, 2) ELSE image_url END)
    
    ELSE image_url
END
WHERE image_url IS NOT NULL
  AND (image_url NOT LIKE 'http://%' AND image_url NOT LIKE 'https://%')
  OR (image_url NOT LIKE '%.%' AND image_url NOT LIKE 'http://%');

-- ตรวจสอบผลลัพธ์
SELECT id, name, image_url FROM fruits WHERE image_url IS NOT NULL;

