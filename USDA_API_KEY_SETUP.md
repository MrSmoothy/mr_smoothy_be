# วิธีตั้งค่า USDA API Key

## ปัญหา: USDA API 403 Forbidden

ถ้าคุณเห็น error `403 Forbidden` จาก USDA API แสดงว่าคุณยังไม่ได้ตั้งค่า API key หรือ API key ไม่ถูกต้อง

## วิธีแก้ไข

### 1. ขอ USDA API Key

1. ไปที่ https://fdc.nal.usda.gov/api-guide.html
2. คลิก "Request an API Key"
3. กรอกข้อมูลและสมัครสมาชิก
4. รับ API key ทาง email

### 2. ตั้งค่า API Key

#### วิธีที่ 1: ใส่ใน docker-compose.yaml (แนะนำ)

แก้ไขไฟล์ `docker-compose.yaml`:

```yaml
environment:
  # ... other variables ...
  USDA_API_KEY: your_actual_usda_api_key_here  # ใส่ API key จริงที่นี่
```

#### วิธีที่ 2: ใส่ใน application.properties

แก้ไขไฟล์ `src/main/resources/application.properties`:

```properties
usda.api.key=your_actual_usda_api_key_here
```

### 3. Restart Application

```bash
docker-compose restart api
```

หรือ rebuild:

```bash
docker-compose up -d --build api
```

## หมายเหตุ

- USDA API key ฟรี แต่ต้องสมัครสมาชิก
- API key จะถูกส่งทาง email หลังจากสมัครสมาชิก
- ถ้ายังไม่มี API key ระบบจะยังทำงานได้ แต่จะไม่สามารถดึงข้อมูลโภชนาการได้
- การดึงข้อมูลโภชนาการเป็น optional - ถ้าไม่สำเร็จ ระบบจะยังสร้างผลไม้ได้แต่ไม่มีข้อมูลโภชนาการ

