# 🔧 แก้ไขปัญหา API Error "An unexpected error occurred"

## ✅ สิ่งที่แก้ไขแล้ว:

1. ✅ เพิ่ม `@CrossOrigin(origins = "*")` ใน Controllers:
   - `PublicCatalogController`
   - `CartController`
   - `OrderController`

2. ✅ สร้าง `UserAuthInterceptor` สำหรับตรวจสอบ authentication ของ user ทั่วไป
   - `/api/cart/**` ต้องการ authentication
   - `/api/orders/**` ต้องการ authentication

3. ✅ ปรับปรุง `GlobalExceptionHandler`:
   - แสดง error message ที่ชัดเจนขึ้น
   - Handle `NoResourceFoundException` แยกต่างหาก

## 📋 Endpoint และ Authentication:

### Public APIs (ไม่ต้อง login):
- ✅ `GET /api/public/fruits`
- ✅ `GET /api/public/cup-sizes`
- ✅ `GET /api/public/drinks`
- ✅ `POST /api/auth/login`
- ✅ `POST /api/auth/register`

### User APIs (ต้อง login):
- 🔒 `GET /api/cart` - ต้องการ token
- 🔒 `POST /api/cart/items` - ต้องการ token
- 🔒 `DELETE /api/cart/items/{id}` - ต้องการ token
- 🔒 `DELETE /api/cart/clear` - ต้องการ token
- 🔒 `GET /api/orders` - ต้องการ token
- 🔒 `POST /api/orders` - ต้องการ token
- 🔒 `GET /api/orders/{id}` - ต้องการ token

### Admin APIs (ต้องเป็น ADMIN):
- 🔒 `GET /api/admin/**` - ต้องการ token และ ADMIN role

## 🔍 วิธีตรวจสอบปัญหา:

### 1. ตรวจสอบว่า API ทำงาน:
```bash
# Public API (ไม่ต้อง token)
curl http://localhost:8080/api/public/fruits

# User API (ต้อง token)
curl http://localhost:8080/api/cart
# ควรได้: {"success":false,"message":"Authentication required..."}

# User API พร้อม token
curl http://localhost:8080/api/cart \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 2. ตรวจสอบ Frontend:
- เปิด Browser DevTools → Network tab
- ดูว่าหน้าเว็บเรียก API endpoint ไหน
- ตรวจสอบว่า Header มี `Authorization: Bearer TOKEN` หรือไม่

### 3. ตรวจสอบ Backend Logs:
```bash
docker-compose logs app --tail 50 | grep ERROR
```

## ⚠️ ปัญหาที่พบบ่อย:

### ปัญหา 1: "Authentication required"
**สาเหตุ**: ไม่มี token หรือ token หมดอายุ

**แก้ไข**:
- Login ใหม่เพื่อรับ token
- ตรวจสอบว่า localStorage มี `auth_token` หรือไม่

### ปัญหา 2: "No static resource api/auth/fruits"
**สาเหตุ**: URL ผิด หรือ endpoint ไม่มี

**แก้ไข**:
- ตรวจสอบว่าเรียก `/api/public/fruits` ไม่ใช่ `/api/auth/fruits`
- ตรวจสอบว่า backend ทำงานอยู่ (`docker-compose ps`)

### ปัญหา 3: CORS Error
**แก้ไข**: ✅ แก้ไขแล้วด้วย `@CrossOrigin`

## 🧪 ทดสอบ:

### Test Public API:
```bash
curl http://localhost:8080/api/public/fruits
```

### Test User API (ต้อง login ก่อน):
```bash
# 1. Login เพื่อรับ token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"your_username","password":"your_password"}' \
  | jq -r '.data.token')

# 2. ใช้ token
curl http://localhost:8080/api/cart \
  -H "Authorization: Bearer $TOKEN"
```

## 📝 หมายเหตุ:

- **Public APIs** (`/api/public/**`) - ไม่ต้อง authentication
- **User APIs** (`/api/cart/**`, `/api/orders/**`) - ต้อง authentication (user ใดก็ได้)
- **Admin APIs** (`/api/admin/**`) - ต้อง authentication + ADMIN role

