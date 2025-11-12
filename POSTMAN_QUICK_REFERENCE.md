# ⚡ Postman Quick Reference Card

## 🔐 Authentication

### Login
```
POST http://localhost:8080/api/auth/login
Headers: Content-Type: application/json
Body: {
  "username": "admin",
  "password": "your_password"
}
```
→ Copy `token` จาก response

---

## 🍎 Fruits

### เพิ่มผลไม้
```
POST http://localhost:8080/api/admin/fruits
Headers: 
  Content-Type: application/json
  Authorization: Bearer YOUR_TOKEN
Body: {
  "name": "กล้วย",
  "description": "กล้วยสุกหวาน",
  "pricePerUnit": 25.00,
  "active": true
}
```

### ดูผลไม้ทั้งหมด
```
GET http://localhost:8080/api/admin/fruits
Headers: Authorization: Bearer YOUR_TOKEN
```

---

## 🥤 Cup Sizes

### เพิ่มขนาดแก้ว
```
POST http://localhost:8080/api/admin/cup-sizes
Headers: 
  Content-Type: application/json
  Authorization: Bearer YOUR_TOKEN
Body: {
  "name": "Small",
  "volumeMl": 300,
  "priceExtra": 0.00,
  "active": true
}
```

### ดูขนาดแก้วทั้งหมด
```
GET http://localhost:8080/api/admin/cup-sizes
Headers: Authorization: Bearer YOUR_TOKEN
```

---

## ☕ Drinks

### เพิ่มเมนู
```
POST http://localhost:8080/api/admin/drinks
Headers: 
  Content-Type: application/json
  Authorization: Bearer YOUR_TOKEN
Body: {
  "name": "Smoothie กล้วย-สตรอเบอรี่",
  "description": "น้ำปั่นรวมกล้วยและสตรอเบอรี่",
  "active": true,
  "ingredients": [
    { "fruitId": 1, "quantity": 2 },
    { "fruitId": 2, "quantity": 3 }
  ]
}
```

### ดูเมนูทั้งหมด
```
GET http://localhost:8080/api/admin/drinks
Headers: Authorization: Bearer YOUR_TOKEN
```

---

## 📸 Images

### อัปโหลดรูปภาพผลไม้
```
POST http://localhost:8080/api/admin/images/upload/fruit
Headers: Authorization: Bearer YOUR_TOKEN
Body: form-data
  Key: file, Type: File
```

→ Copy `url` จาก response ไปใส่ใน `imageUrl`

---

## 📝 JSON Templates

### ผลไม้
```json
{
  "name": "ชื่อผลไม้",
  "description": "คำอธิบาย",
  "pricePerUnit": 25.00,
  "imageUrl": null,
  "active": true
}
```

### ขนาดแก้ว
```json
{
  "name": "Small",
  "volumeMl": 300,
  "priceExtra": 0.00,
  "active": true
}
```

### เมนู
```json
{
  "name": "ชื่อเมนู",
  "description": "คำอธิบาย",
  "active": true,
  "ingredients": [
    { "fruitId": 1, "quantity": 2 }
  ]
}
```

---

## ✅ Order of Operations

1. Login → Get Token
2. Add Fruits → Get Fruit IDs
3. Add Cup Sizes
4. Add Drinks (ใช้ Fruit IDs จากขั้น 2)
5. Upload Images (ถ้าต้องการ)

---

## 🔗 Base URL
```
http://localhost:8080
```

