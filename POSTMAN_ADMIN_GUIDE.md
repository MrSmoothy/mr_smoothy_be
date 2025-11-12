# 📋 วิธีเพิ่มผลไม้/เมนู/ขนาดแก้วผ่าน Postman

## 🔐 1. Login เพื่อรับ Token

### Request
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json
```

### Body
```json
{
  "username": "your_admin_username",
  "password": "your_password"
}
```

### Response
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "role": "ADMIN"
    }
  }
}
```

**Copy `token` จาก response ไปใช้ใน requests ต่อไป**

---

## 🍎 2. เพิ่มผลไม้ (Fruit)

### Request
```
POST http://localhost:8080/api/admin/fruits
Authorization: Bearer YOUR_TOKEN_HERE
Content-Type: application/json
```

### Body
```json
{
  "name": "กล้วย",
  "description": "กล้วยสุกหวาน อุดมไปด้วยโพแทสเซียมและวิตามิน B6",
  "pricePerUnit": 25.00,
  "imageUrl": null,
  "active": true
}
```

### ตัวอย่างเพิ่มผลไม้อื่นๆ:
```json
{
  "name": "สตรอเบอรี่",
  "description": "สตรอเบอรี่สด หวานอมเปรี้ยว อุดมด้วยวิตามิน C",
  "pricePerUnit": 35.00,
  "active": true
}
```

```json
{
  "name": "เลม่อน",
  "description": "เลม่อนสด เปรี้ยวสดชื่น อุดมด้วยวิตามิน C",
  "pricePerUnit": 20.00,
  "active": true
}
```

---

## ☕ 3. เพิ่มเมนู (Predefined Drink)

### Request
```
POST http://localhost:8080/api/admin/drinks
Authorization: Bearer YOUR_TOKEN_HERE
Content-Type: application/json
```

### Body
```json
{
  "name": "Smoothie กล้วย-สตรอเบอรี่",
  "description": "น้ำปั่นรวมกล้วยและสตรอเบอรี่ หวานสดชื่น",
  "imageUrl": null,
  "active": true,
  "ingredients": [
    {
      "fruitId": 1,
      "quantity": 2
    },
    {
      "fruitId": 2,
      "quantity": 3
    }
  ]
}
```

**หมายเหตุ**: `fruitId` ต้องเป็น ID ของผลไม้ที่มีอยู่ในระบบแล้ว

---

## 🥤 4. เพิ่มขนาดแก้ว (Cup Size)

### Request
```
POST http://localhost:8080/api/admin/cup-sizes
Authorization: Bearer YOUR_TOKEN_HERE
Content-Type: application/json
```

### Body
```json
{
  "name": "Small",
  "volumeMl": 300,
  "priceExtra": 0.00,
  "active": true
}
```

### ตัวอย่างเพิ่มขนาดแก้วอื่นๆ:
```json
{
  "name": "Medium",
  "volumeMl": 500,
  "priceExtra": 10.00,
  "active": true
}
```

```json
{
  "name": "Large",
  "volumeMl": 700,
  "priceExtra": 20.00,
  "active": true
}
```

---

## 📸 5. อัปโหลดรูปภาพ

### อัปโหลดรูปผลไม้
```
POST http://localhost:8080/api/admin/images/upload/fruit
Authorization: Bearer YOUR_TOKEN_HERE
Content-Type: multipart/form-data
```

**Body** (form-data):
- `file`: [เลือกไฟล์ภาพ]

**Response**:
```json
{
  "success": true,
  "message": "Image uploaded successfully",
  "data": {
    "url": "http://localhost:9000/mr-smoothy-images/fruits/uuid.png",
    "filename": "banana.png"
  }
}
```

**Copy `url` ไปใส่ใน `imageUrl` เมื่อสร้างผลไม้**

---

## 📝 6. ดูข้อมูลทั้งหมด

### ดูผลไม้ทั้งหมด
```
GET http://localhost:8080/api/admin/fruits
Authorization: Bearer YOUR_TOKEN_HERE
```

### ดูเมนูทั้งหมด
```
GET http://localhost:8080/api/admin/drinks
Authorization: Bearer YOUR_TOKEN_HERE
```

### ดูขนาดแก้วทั้งหมด
```
GET http://localhost:8080/api/admin/cup-sizes
Authorization: Bearer YOUR_TOKEN_HERE
```

---

## ✏️ 7. แก้ไขข้อมูล

### แก้ไขผลไม้
```
PUT http://localhost:8080/api/admin/fruits/{id}
Authorization: Bearer YOUR_TOKEN_HERE
Content-Type: application/json

{
  "name": "กล้วยหอม",
  "description": "อัปเดตคำอธิบาย",
  "pricePerUnit": 30.00
}
```

### แก้ไขเมนู
```
PUT http://localhost:8080/api/admin/drinks/{id}
Authorization: Bearer YOUR_TOKEN_HERE
Content-Type: application/json

{
  "name": "Smoothie กล้วย-สตรอเบอรี่ (อัปเดต)",
  "description": "คำอธิบายใหม่",
  "active": false
}
```

---

## 🗑️ 8. ลบข้อมูล

### ลบผลไม้
```
DELETE http://localhost:8080/api/admin/fruits/{id}
Authorization: Bearer YOUR_TOKEN_HERE
```

### ลบเมนู
```
DELETE http://localhost:8080/api/admin/drinks/{id}
Authorization: Bearer YOUR_TOKEN_HERE
```

### ลบขนาดแก้ว
```
DELETE http://localhost:8080/api/admin/cup-sizes/{id}
Authorization: Bearer YOUR_TOKEN_HERE
```

---

## ⚠️ หมายเหตุ

1. **ต้องเป็น ADMIN**: ทุก API ต้องใช้ token ของ user ที่มี role = "ADMIN"
2. **Token หมดอายุ**: ถ้า token หมดอายุต้อง login ใหม่
3. **ID ต้องมีอยู่จริง**: เมื่อเพิ่มเมนู ต้องใช้ `fruitId` ที่มีอยู่จริงในระบบ
4. **ตรวจสอบก่อนลบ**: การลบข้อมูลไม่สามารถกู้คืนได้

---

## 📚 JSON Samples

ดูไฟล์ `FRUITS_JSON_SAMPLES.md` หรือ `simple_fruits_batch.json` สำหรับตัวอย่าง JSON เพิ่มเติม

