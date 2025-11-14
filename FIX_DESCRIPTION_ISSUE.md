# แก้ไขปัญหา Description ไม่แสดงเมื่อ Admin กดแก้ไข

## ปัญหา
เมื่อ Admin กดแก้ไขผลไม้หรือน้ำปั่น คำอธิบาย (description) ไม่แสดงออกมาในฟอร์ม

## สาเหตุ
1. Database ไม่มี description column
2. JPA Entity ไม่ได้ map กับ database column อย่างถูกต้อง

## การแก้ไข

### 1. รัน SQL Migration Scripts

#### สำหรับ Fruits Table:
```sql
ALTER TABLE fruits 
ADD COLUMN description TEXT NULL;
```

#### สำหรับ Predefined Drinks Table:
```sql
ALTER TABLE predefined_drinks 
ADD COLUMN description TEXT NULL;
```

### 2. Entity Classes (แก้ไขแล้ว)

#### Fruit.java
- เพิ่ม `@Column(name = "description", columnDefinition = "TEXT", nullable = true)`
- ตั้ง `nullable = true` เพื่อรองรับ null values

#### PredefinedDrink.java
- เพิ่ม `@Column(name = "description", columnDefinition = "TEXT", nullable = true)`
- ตั้ง `nullable = true` เพื่อรองรับ null values

### 3. Service Classes (แก้ไขแล้ว)

#### FruitService.java
- `toResponse()` method ส่ง description กลับมาแล้ว
- `updateFruitFields()` method อัพเดต description เมื่อได้รับค่า

#### PredefinedDrinkService.java
- `toResponse()` method ส่ง description กลับมาแล้ว
- `updateDrinkFields()` method อัพเดต description เมื่อได้รับค่า

### 4. Frontend (แก้ไขแล้ว)

#### admin/fruits/page.tsx
- `openModal()` method แปลง description เป็น string (รองรับ null)
- Form แสดง description ใน textarea

#### admin/drinks/page.tsx
- `openModal()` method แปลง description เป็น string (รองรับ null)
- Form แสดง description ใน textarea

## วิธีทดสอบ

1. รัน SQL migration scripts
2. Restart backend application
3. ทดสอบ:
   - สร้างผลไม้ใหม่พร้อม description
   - กดแก้ไขผลไม้ - description ควรแสดงในฟอร์ม
   - แก้ไข description และบันทึก
   - กดแก้ไขอีกครั้ง - description ควรแสดงค่าที่อัพเดตแล้ว

## สิ่งที่ต้องทำ

1. **รัน SQL Migration Scripts** (สำคัญที่สุด!)
   ```bash
   # เชื่อมต่อ database และรัน SQL scripts
   mysql -u username -p database_name < add_description_column.sql
   mysql -u username -p database_name < add_description_column_to_drinks.sql
   ```

2. **Restart Backend Application**
   ```bash
   cd mr_smoothy_be
   ./mvnw spring-boot:run
   ```

3. **ทดสอบ**
   - สร้างผลไม้/น้ำปั่นใหม่พร้อม description
   - แก้ไขผลไม้/น้ำปั่นที่มี description
   - ตรวจสอบว่า description แสดงในฟอร์ม

## หมายเหตุ

- ถ้า database ยังไม่มี description column JPA จะไม่สามารถ map field กับ column ได้
- ต้องรัน SQL migration scripts ก่อน restart application
- ถ้าข้อมูลใน database เป็น null description จะแสดงเป็น empty string ในฟอร์ม (ถูกต้อง)

