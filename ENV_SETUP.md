# การตั้งค่า Environment Variables

โปรเจกต์นี้ใช้ไฟล์ `.env` สำหรับเก็บค่าตัวแปรทั้งหมด เพื่อความสะดวกในการจัดการและไม่ให้ต้องแก้หลายที่

## ขั้นตอนการตั้งค่า

1. **สร้างไฟล์ `.env`** โดยคัดลอกจาก `.env.example`:
   ```bash
   cp .env.example .env
   ```
   
   หรือใน Windows PowerShell:
   ```powershell
   Copy-Item .env.example .env
   ```

2. **แก้ไขค่าในไฟล์ `.env`** ตามต้องการ:
   ```env
   # Database Configuration
   MYSQL_ROOT_PASSWORD=rootpass
   MYSQL_DATABASE=mr_smoothy
   MYSQL_USER=smoothy
   MYSQL_PASSWORD=smoothypass
   DB_PORT=3306
   
   # Spring Boot Application
   APP_PORT=8080
   SPRING_PROFILES_ACTIVE=dev
   
   # JWT Configuration
   JWT_SECRET=myVeryStrongSecretKey_ChangeMe_AtLeast256Bits________________________________
   JWT_EXPIRATION=86400000
   
   # MinIO Configuration
   MINIO_ROOT_USER=minioadmin
   MINIO_ROOT_PASSWORD=minioadmin123
   MINIO_ACCESS_KEY=minioadmin
   MINIO_SECRET_KEY=minioadmin123
   MINIO_ENDPOINT=http://minio:9000
   MINIO_PUBLIC_ENDPOINT=http://localhost:9000
   MINIO_BUCKET_NAME=mr-smoothy-images
   MINIO_API_PORT=9000
   MINIO_CONSOLE_PORT=9001
   
   # phpMyAdmin
   PMA_PORT=8081
   ```

3. **ไฟล์ `.env` จะถูกโหลดอัตโนมัติ** เมื่อรัน `docker compose up`

## ตัวแปรที่สำคัญ

- **Database**: `MYSQL_ROOT_PASSWORD`, `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`
- **JWT**: `JWT_SECRET` (ควรเปลี่ยนใน production)
- **MinIO**: `MINIO_ROOT_USER`, `MINIO_ROOT_PASSWORD` (ควรเปลี่ยนใน production)
- **Ports**: `APP_PORT`, `DB_PORT`, `PMA_PORT`, `MINIO_API_PORT`, `MINIO_CONSOLE_PORT`

## หมายเหตุ

- ไฟล์ `.env` ควรถูกเพิ่มใน `.gitignore` เพื่อป้องกันการ commit ค่าลับขึ้น repository
- ใช้ `.env.example` เป็น template สำหรับ团队成员

