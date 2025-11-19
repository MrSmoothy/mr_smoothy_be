package project.mr_smoothy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "ingredients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fruit {

    public enum Category {
        FRUIT,      // ผลไม้
        VEGETABLE,  // ผัก
        ADDON       // ส่วนเสริม เช่น โยเกิร์ต, น้ำผึ้ง, นม
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

    @Column(nullable = false)
    private BigDecimal pricePerUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category = Category.FRUIT;

    @Column(nullable = false)
    private Boolean active = true;
}


