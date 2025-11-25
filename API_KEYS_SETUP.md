# การตั้งค่า API Keys

## ⚠️ สำคัญ: อย่า Commit API Keys ลง Git!

GitHub จะบล็อกการ push ถ้าตรวจพบ API keys ในโค้ด เพื่อความปลอดภัย

## วิธีตั้งค่า API Keys

### วิธีที่ 1: ใช้ docker-compose.yaml (แนะนำสำหรับ Development)

1. เปิดไฟล์ `docker-compose.yaml`
2. แก้ไขค่า API keys ในส่วน environment ของ service `api`:

```yaml
environment:
  USDA_API_KEY: your_usda_api_key_here  # ใส่ USDA API key ของคุณ
  OPENAI_API_KEY: your_openai_api_key_here  # ใส่ OpenAI API key ของคุณ
  OPENAI_MODEL: gpt-4-turbo-preview
```

3. **อย่า commit ไฟล์ `docker-compose.yaml` ที่มี API keys จริง!**

### วิธีที่ 2: ใช้ Environment Variables (แนะนำสำหรับ Production)

ตั้งค่า environment variables ในระบบ:

```bash
export USDA_API_KEY=your_usda_api_key_here
export OPENAI_API_KEY=your_openai_api_key_here
export OPENAI_MODEL=gpt-4-turbo-preview
```

จากนั้น docker-compose จะอ่านค่าจาก environment variables อัตโนมัติ

### วิธีที่ 3: ใช้ .env file

1. สร้างไฟล์ `.env` ในโฟลเดอร์ `mr_smoothy_be/`
2. เพิ่ม API keys:

```env
USDA_API_KEY=your_usda_api_key_here
OPENAI_API_KEY=your_openai_api_key_here
OPENAI_MODEL=gpt-4-turbo-preview
```

3. ไฟล์ `.env` จะถูก ignore โดย `.gitignore` อัตโนมัติ

## รับ API Keys

### USDA FoodData Central API
- ไปที่: https://fdc.nal.usda.gov/api-guide.html
- สมัครและรับ API key ฟรี

### OpenAI API
- ไปที่: https://platform.openai.com/api-keys
- สร้าง API key ใหม่ (ต้องมีบัญชี OpenAI)

## ตรวจสอบว่า API Keys ทำงาน

หลังจากตั้งค่าแล้ว รีสตาร์ท containers:

```bash
docker-compose down
docker-compose up -d
```

ลองเพิ่มผลไม้ใหม่ในหน้า `/admin/fruits` และตรวจสอบว่าข้อมูลโภชนาการถูกดึงมาอัตโนมัติ

