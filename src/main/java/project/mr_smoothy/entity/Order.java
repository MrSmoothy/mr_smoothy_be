package project.mr_smoothy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    public enum OrderStatus {
        PENDING,    // รอการยืนยัน
        CONFIRMED,  // ยืนยันแล้ว
        PREPARING,  // กำลังเตรียม
        READY,      // พร้อมรับ
        COMPLETED,  // รับแล้ว
        CANCELLED   // ยกเลิก
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = true)
    @JoinColumn(name = "user_id")
    private User user; // null สำหรับ guest orders

    @Column(name = "customer_name")
    private String customerName; // สำหรับ guest orders

    @Column(name = "customer_email")
    private String customerEmail; // สำหรับ guest orders

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "pickup_time")
    private LocalDateTime pickupTime;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

