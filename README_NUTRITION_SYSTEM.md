# Smoothy Ingredient Management & Smart Nutrition System

## 📋 ภาพรวมระบบ

ระบบนี้เพิ่มฟีเจอร์การจัดการข้อมูลโภชนาการและรสชาติของวัถุดิบโดยใช้:
- **USDA FoodData Central API** - ดึงข้อมูลโภชนาการอย่างเป็นทางการ
- **OpenAI API** - ประมวลผลและเพิ่มข้อมูลรสชาติ การจับคู่ และคำอธิบาย

## 🚀 การตั้งค่า

### 1. Database Migration

รัน SQL script เพื่อเพิ่ม columns สำหรับ nutrition data:

```bash
mysql -u your_username -p your_database < add_nutrition_columns_to_ingredients.sql
```

### 2. ตั้งค่า API Keys

แก้ไขไฟล์ `src/main/resources/application.properties`:

```properties
# USDA FoodData Central API
usda.api.key=your_usda_api_key_here
usda.api.base-url=https://api.nal.usda.gov/fdc/v1

# OpenAI API Configuration
# IMPORTANT: Set OPENAI_API_KEY in docker-compose.yaml or environment variable
# Do NOT commit API keys to git!
openai.api.key=${OPENAI_API_KEY:your_openai_api_key_here}
openai.api.model=${OPENAI_MODEL:gpt-4-turbo-preview}
openai.api.base-url=https://api.openai.com/v1
```

**วิธีรับ USDA API Key:**
1. ไปที่ https://fdc.nal.usda.gov/api-guide.html
2. สมัครสมาชิกและขอ API key
3. ใส่ API key ใน `application.properties`

**OpenAI API Key:**
- ใช้ key ที่ให้มาแล้ว หรือไปที่ https://platform.openai.com/api-keys เพื่อสร้างใหม่

### 3. Dependencies

ระบบใช้ dependencies ต่อไปนี้ (เพิ่มใน `pom.xml` แล้ว):
- `spring-boot-starter-webflux` - สำหรับเรียก USDA API
- `com.theokanning.openai-gpt3-java` - OpenAI Java SDK

## 📡 API Endpoints

### Admin: เพิ่ม Ingredient พร้อม Nutrition Data

**POST** `/api/admin/ingredient`

**Request:**
```json
{
  "name": "Banana",
  "description": "กล้วยหอม",
  "imageUrl": "https://...",
  "pricePerUnit": 10.00,
  "category": "FRUIT",
  "active": true,
  "seasonal": false
}
```

**Response:**
```json
{
  "success": true,
  "message": "Ingredient added successfully with nutrition data",
  "data": {
    "id": 1,
    "name": "Banana",
    "calorie": 89.0,
    "protein": 1.1,
    "fiber": 2.6,
    "vitamins": "{\"vitaminC\": 8.7, \"vitaminA\": 3}",
    "minerals": "{\"calcium\": 5, \"iron\": 0.26}",
    "flavorProfile": "sweet, tropical",
    "tasteNotes": "Rich, creamy sweetness with tropical notes",
    "bestMixPairing": "[\"strawberry\", \"mango\", \"yogurt\"]",
    "avoidPairing": "[\"citrus\", \"dairy\"]"
  }
}
```

### User: คำนวณ Smoothy Nutrition และ Flavor

**POST** `/api/smoothy/calc`

**Request:**
```json
{
  "ingredients": [
    {
      "ingredientId": 1,
      "amount": 100
    },
    {
      "ingredientId": 2,
      "amount": 50
    }
  ]
}
```

**Response:**
```json
{
  "success": true,
  "message": "Smoothy calculated successfully",
  "data": {
    "totalNutrition": {
      "totalCalorie": 120.5,
      "totalProtein": 2.3,
      "totalFiber": 4.1
    },
    "flavorDescription": "A refreshing tropical blend with sweet banana and tangy strawberry...",
    "synergy": [
      "Banana and strawberry create a perfect sweet-tart balance",
      "High fiber content aids digestion"
    ],
    "cancellation": [
      "Avoid adding dairy as it may curdle with acidic fruits"
    ]
  }
}
```

## 🎨 Frontend Pages

### 1. Admin: เพิ่ม Ingredient
**URL:** `/admin/add-ingredient`

- ฟอร์มสำหรับเพิ่มวัถุดิบ
- ระบบจะเรียก USDA API และ OpenAI อัตโนมัติ
- แสดงผลลัพธ์ nutrition data และ flavor profile

### 2. User: สร้าง Smoothy แบบกำหนดเอง
**URL:** `/smoothy/custom`

- เลือกวัถุดิบและกำหนดจำนวน (กรัม)
- คำนวณโภชนาการรวม
- วิเคราะห์รสชาติและผลกระทบ

## 🔄 Flow การทำงาน

### การเพิ่ม Ingredient:

1. Admin ใส่ชื่อวัถุดิบ (ภาษาอังกฤษ)
2. Backend เรียก USDA API `/foods/search` เพื่อหา fdcId
3. Backend เรียก USDA API `/food/{fdcId}` เพื่อดึงข้อมูลโภชนาการ
4. Backend ส่ง USDA data ไป OpenAI เพื่อประมวลผล
5. OpenAI ส่งกลับ structured JSON พร้อม:
   - Nutrition data (calorie, protein, fiber, vitamins, minerals)
   - Flavor profile และ taste notes
   - Best mix pairing และ avoid pairing
6. Backend บันทึกข้อมูลทั้งหมดลง database
7. ส่ง response กลับไป frontend

### การคำนวณ Smoothy:

1. User เลือกวัถุดิบและกำหนดจำนวน (กรัม)
2. Frontend ส่ง ingredient IDs และ amounts ไป backend
3. Backend ดึงข้อมูลโภชนาการจาก database
4. คำนวณโภชนาการรวม (calorie, protein, fiber)
5. ส่งข้อมูลวัถุดิบไป OpenAI เพื่อวิเคราะห์รสชาติ
6. OpenAI ส่งกลับ:
   - Flavor description
   - Synergy effects
   - Cancellation warnings
7. Backend ส่งผลลัพธ์กลับไป frontend

## 📝 หมายเหตุ

- ข้อมูลโภชนาการจาก USDA เป็นข้อมูลต่อ 100g
- ระบบคำนวณโภชนาการตามจำนวนที่ผู้ใช้เลือก
- OpenAI จะใช้ model `gpt-4-turbo-preview` (สามารถเปลี่ยนได้ใน `application.properties`)

## 🐛 Troubleshooting

### USDA API ไม่ทำงาน:
- ตรวจสอบ API key ใน `application.properties`
- ตรวจสอบ network connection
- ตรวจสอบว่าใช้ชื่อภาษาอังกฤษในการค้นหา

### OpenAI API ไม่ทำงาน:
- ตรวจสอบ API key
- ตรวจสอบว่า account มี credit เพียงพอ
- ตรวจสอบ rate limits

### Database Error:
- ตรวจสอบว่าได้รัน migration script แล้ว
- ตรวจสอบว่า MySQL version รองรับ JSON columns

