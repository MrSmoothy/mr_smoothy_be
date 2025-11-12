# 📋 คู่มือใช้ Postman เพิ่มข้อมูลเข้า Mr. Smoothy System

## 🎯 ขั้นตอนทั้งหมด

---

## ขั้นที่ 1: Login เพื่อรับ Token

### 1.1 สร้าง Request ใหม่
- เปิด Postman
- กด `+` หรือ `New` → `HTTP Request`
- ตั้งชื่อ: `Login - Get Token`

### 1.2 ตั้งค่า Request
**Method**: `POST`

**URL**: 
```
http://localhost:8080/api/auth/login
```

**Headers**:
- กดแท็บ `Headers`
- เพิ่ม:
  - Key: `Content-Type`
  - Value: `application/json`

**Body**:
- กดแท็บ `Body`
- เลือก `raw`
- เลือก `JSON` จาก dropdown ด้านขวา
- วาง JSON นี้:

```json
{
  "username": "admin",
  "password": "your_password"
}
```

> **หมายเหตุ**: แทนที่ `admin` และ `your_password` ด้วย username และ password จริงของคุณ

### 1.3 ส่ง Request และ Copy Token
- กดปุ่ม `Send`
- ดู Response (ควรจะได้ JSON ที่มี `token`)
- **Copy `token` จาก response** (จะใช้ในขั้นตอนต่อไป)

**Response ตัวอย่าง**:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTcwMDAwMDAwMH0...",
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "role": "ADMIN"
    }
  }
}
```

---

## ขั้นที่ 2: เพิ่มผลไม้ (Fruits)

### 2.1 สร้าง Request ใหม่
- กด `New` → `HTTP Request`
- ตั้งชื่อ: `Add Fruit - Banana`

### 2.2 ตั้งค่า Request
**Method**: `POST`

**URL**: 
```
http://localhost:8080/api/admin/fruits
```

**Headers**:
- `Content-Type`: `application/json`
- `Authorization`: `Bearer YOUR_TOKEN_HERE`
  > แทนที่ `YOUR_TOKEN_HERE` ด้วย token ที่ copy จากขั้นที่ 1

**Body** (raw, JSON):
```json
{
  "name": "กล้วย",
  "description": "กล้วยสุกหวาน อุดมไปด้วยโพแทสเซียมและวิตามิน B6",
  "pricePerUnit": 25.00,
  "imageUrl": null,
  "active": true
}
```

### 2.3 ส่ง Request
- กด `Send`
- ถ้าสำเร็จจะได้ Response:
```json
{
  "success": true,
  "message": "Created",
  "data": {
    "id": 1,
    "name": "กล้วย",
    "description": "กล้วยสุกหวาน อุดมไปด้วยโพแทสเซียมและวิตามิน B6",
    "pricePerUnit": 25.00,
    "imageUrl": null,
    "active": true
  }
}
```

### 2.4 ตัวอย่างผลไม้อื่นๆ

**สตรอเบอรี่**:
```json
{
  "name": "สตรอเบอรี่",
  "description": "สตรอเบอรี่สด หวานอมเปรี้ยว อุดมด้วยวิตามิน C",
  "pricePerUnit": 35.00,
  "active": true
}
```

**เลม่อน**:
```json
{
  "name": "เลม่อน",
  "description": "เลม่อนสด เปรี้ยวสดชื่น อุดมด้วยวิตามิน C",
  "pricePerUnit": 20.00,
  "active": true
}
```

**บลูเบอรี่**:
```json
{
  "name": "บลูเบอรี่",
  "description": "บลูเบอรี่สด หวาน อุดมด้วยสารต้านอนุมูลอิสระ",
  "pricePerUnit": 40.00,
  "active": true
}
```

**บร็อคโคลี่**:
```json
{
  "name": "บร็อคโคลี่",
  "description": "บร็อคโคลี่สด กรอบ อุดมด้วยไฟเบอร์และวิตามิน K",
  "pricePerUnit": 30.00,
  "active": true
}
```

---

## ขั้นที่ 3: เพิ่มขนาดแก้ว (Cup Sizes)

### 3.1 สร้าง Request ใหม่
- ตั้งชื่อ: `Add Cup Size - Small`

### 3.2 ตั้งค่า Request
**Method**: `POST`

**URL**: 
```
http://localhost:8080/api/admin/cup-sizes
```

**Headers**:
- `Content-Type`: `application/json`
- `Authorization`: `Bearer YOUR_TOKEN_HERE`

**Body** (raw, JSON):
```json
{
  "name": "Small",
  "volumeMl": 300,
  "priceExtra": 0.00,
  "active": true
}
```

### 3.3 ตัวอย่างขนาดแก้วอื่นๆ

**Medium**:
```json
{
  "name": "Medium",
  "volumeMl": 500,
  "priceExtra": 10.00,
  "active": true
}
```

**Large**:
```json
{
  "name": "Large",
  "volumeMl": 700,
  "priceExtra": 20.00,
  "active": true
}
```

**XL**:
```json
{
  "name": "XL",
  "volumeMl": 1000,
  "priceExtra": 30.00,
  "active": true
}
```

---

## ขั้นที่ 4: เพิ่มเมนู (Predefined Drinks)

### 4.1 สร้าง Request ใหม่
- ตั้งชื่อ: `Add Drink - Banana Strawberry Smoothie`

### 4.2 ตั้งค่า Request
**Method**: `POST`

**URL**: 
```
http://localhost:8080/api/admin/drinks
```

**Headers**:
- `Content-Type`: `application/json`
- `Authorization`: `Bearer YOUR_TOKEN_HERE`

**Body** (raw, JSON):
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

> **สำคัญ**: `fruitId` ต้องเป็น ID ของผลไม้ที่มีอยู่ในระบบแล้ว ถ้าไม่แน่ใจ ID ให้ใช้ API `GET /api/admin/fruits` ดูก่อน

### 4.3 วิธีหา Fruit ID

**Request**:
```
GET http://localhost:8080/api/admin/fruits
Authorization: Bearer YOUR_TOKEN_HERE
```

**Response**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "กล้วย",
      ...
    },
    {
      "id": 2,
      "name": "สตรอเบอรี่",
      ...
    }
  ]
}
```

### 4.4 ตัวอย่างเมนูอื่นๆ

**Green Smoothie**:
```json
{
  "name": "Green Smoothie",
  "description": "น้ำปั่นสีเขียวจากบร็อคโคลี่และผักใบเขียว",
  "active": true,
  "ingredients": [
    {
      "fruitId": 5,
      "quantity": 2
    }
  ]
}
```

**Mixed Berry Smoothie**:
```json
{
  "name": "Mixed Berry Smoothie",
  "description": "น้ำปั่นรวมเบอรี่",
  "active": true,
  "ingredients": [
    {
      "fruitId": 2,
      "quantity": 3
    },
    {
      "fruitId": 4,
      "quantity": 2
    }
  ]
}
```

---

## ขั้นที่ 5: อัปโหลดรูปภาพ (ถ้าต้องการ)

### 5.1 สร้าง Request ใหม่
- ตั้งชื่อ: `Upload Fruit Image`

### 5.2 ตั้งค่า Request
**Method**: `POST`

**URL**: 
```
http://localhost:8080/api/admin/images/upload/fruit
```

**Headers**:
- **อย่าเพิ่ม** `Content-Type` (ให้ Postman ตั้งให้อัตโนมัติ)
- `Authorization`: `Bearer YOUR_TOKEN_HERE`

**Body**:
- กดแท็บ `Body`
- เลือก `form-data`
- เพิ่ม key `file` → เลือก Type เป็น `File` → กด `Select Files` เลือกรูปภาพ

### 5.3 ส่ง Request
- กด `Send`
- จะได้ Response:
```json
{
  "success": true,
  "message": "Image uploaded successfully",
  "data": {
    "url": "http://localhost:9000/mr-smoothy-images/fruits/uuid-xxxxx.png",
    "filename": "banana.png"
  }
}
```

**Copy `url` ไปใส่ใน `imageUrl` เมื่อสร้างผลไม้หรือเมนู**

---

## 📝 ตัวอย่าง JSON สำหรับเพิ่มข้อมูลจำนวนมาก

### ผลไม้ (25 ชนิด)
ดูไฟล์: `simple_fruits_batch.json`

### ขนาดแก้ว (4 ขนาด)
```json
[
  {
    "name": "Small",
    "volumeMl": 300,
    "priceExtra": 0.00,
    "active": true
  },
  {
    "name": "Medium",
    "volumeMl": 500,
    "priceExtra": 10.00,
    "active": true
  },
  {
    "name": "Large",
    "volumeMl": 700,
    "priceExtra": 20.00,
    "active": true
  },
  {
    "name": "XL",
    "volumeMl": 1000,
    "priceExtra": 30.00,
    "active": true
  }
]
```

---

## ✅ Checklist ก่อนเริ่ม

- [ ] Backend รันอยู่ (ตรวจสอบ: `docker-compose ps`)
- [ ] มี ADMIN account แล้ว (ถ้าไม่มี ให้สร้างผ่าน Register หรือ SQL)
- [ ] Postman ติดตั้งแล้ว
- [ ] รู้ username และ password ของ ADMIN

---

## 🔍 API Endpoints ทั้งหมด

### Authentication
- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Register (สำหรับ USER เท่านั้น)

### Admin - Fruits
- `GET /api/admin/fruits` - ดูผลไม้ทั้งหมด
- `GET /api/admin/fruits/{id}` - ดูผลไม้ตาม ID
- `POST /api/admin/fruits` - เพิ่มผลไม้
- `PUT /api/admin/fruits/{id}` - แก้ไขผลไม้
- `DELETE /api/admin/fruits/{id}` - ลบผลไม้

### Admin - Drinks
- `GET /api/admin/drinks` - ดูเมนูทั้งหมด
- `GET /api/admin/drinks/{id}` - ดูเมนูตาม ID
- `POST /api/admin/drinks` - เพิ่มเมนู
- `PUT /api/admin/drinks/{id}` - แก้ไขเมนู
- `DELETE /api/admin/drinks/{id}` - ลบเมนู

### Admin - Cup Sizes
- `GET /api/admin/cup-sizes` - ดูขนาดแก้วทั้งหมด
- `GET /api/admin/cup-sizes/{id}` - ดูขนาดแก้วตาม ID
- `POST /api/admin/cup-sizes` - เพิ่มขนาดแก้ว
- `PUT /api/admin/cup-sizes/{id}` - แก้ไขขนาดแก้ว
- `DELETE /api/admin/cup-sizes/{id}` - ลบขนาดแก้ว

### Admin - Images
- `POST /api/admin/images/upload` - อัปโหลดรูปภาพทั่วไป
- `POST /api/admin/images/upload/fruit` - อัปโหลดรูปภาพผลไม้
- `POST /api/admin/images/upload/drink` - อัปโหลดรูปภาพเมนู

---

## ⚠️ ข้อควรระวัง

1. **Token หมดอายุ**: ถ้าได้ error `Invalid or expired token` ต้อง login ใหม่
2. **ต้องเป็น ADMIN**: ถ้าได้ error `Access denied` แสดงว่า user ไม่ใช่ ADMIN
3. **Fruit ID ต้องถูกต้อง**: เมื่อเพิ่มเมนู ต้องใช้ `fruitId` ที่มีอยู่จริง
4. **ชื่อซ้ำ**: ถ้าเพิ่มผลไม้ชื่อซ้ำจะได้ error (ระบบตรวจสอบให้)

---

## 🎯 Quick Start Example

### ตัวอย่างการเพิ่มข้อมูลครบชุด

1. **Login**: `POST /api/auth/login`
2. **เพิ่มผลไม้ 5 ชนิด**: `POST /api/admin/fruits` (5 ครั้ง)
3. **เพิ่มขนาดแก้ว 3 ขนาด**: `POST /api/admin/cup-sizes` (3 ครั้ง)
4. **ดูผลไม้ทั้งหมด**: `GET /api/admin/fruits` (เพื่อหา ID)
5. **เพิ่มเมนู**: `POST /api/admin/drinks` (ใช้ fruitId จากขั้น 4)

---

## 📞 Troubleshooting

### Error: "Authentication required"
→ ตรวจสอบว่าเพิ่ม Header `Authorization: Bearer TOKEN` แล้ว

### Error: "Access denied. Admin role required"
→ ตรวจสอบว่า user ที่ login มี role = "ADMIN"

### Error: "Fruit name already exists"
→ เปลี่ยนชื่อผลไม้

### Error: "Fruit not found"
→ ตรวจสอบว่า `fruitId` ที่ใช้มีอยู่จริงในระบบ

---

พร้อมใช้งานแล้ว! 🚀

