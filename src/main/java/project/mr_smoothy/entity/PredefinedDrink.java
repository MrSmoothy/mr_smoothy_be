package project.mr_smoothy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "predefined_drinks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredefinedDrink {

    public enum Category {
        CLASSIC,           // คลาสสิก
        FRUITY,            // ผลไม้
        HEALTHY,           // สุขภาพ
        ENERGY,            // ให้พลังงาน
        DESSERT,           // ของหวาน
        REFRESHING,        // ดับกระหาย
        SMOOTHIE,          // สมูทตี้
        OTHER              // อื่นๆ
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "description", columnDefinition = "LONGTEXT", nullable = true, updatable = true, insertable = true)
    private String description;
    
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal basePrice; // ราคาพื้นฐาน (ถ้าไม่กำหนดจะคำนวณจากส่วนผสม)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Category category = Category.OTHER;

    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "drink", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PredefinedDrinkFruit> ingredients = new ArrayList<>();
}


