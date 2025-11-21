package project.mr_smoothy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    public enum ItemType { PREDEFINED, CUSTOM }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType type;

    @ManyToOne
    @JoinColumn(name = "predefined_drink_id")
    private PredefinedDrink predefinedDrink; // ใช้เมื่อ type = PREDEFINED

    @ManyToOne(optional = false)
    @JoinColumn(name = "cup_size_id")
    private CupSize cupSize;

    @OneToMany(mappedBy = "cartItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @org.hibernate.annotations.BatchSize(size = 20)
    private List<CartItemFruit> fruits = new ArrayList<>(); // ใช้เมื่อ type = CUSTOM

    @Column(nullable = false)
    private Integer quantity; // จำนวนแก้วของ item นี้

    public BigDecimal getUnitPrice() {
        BigDecimal cupPrice = cupSize.getPrice();
        if (type == ItemType.PREDEFINED) {
            // คำนวณราคาจากผลรวมของผลไม้ใน predefined drink
            BigDecimal drinkFruitSum = predefinedDrink.getIngredients().stream()
                    .map(df -> df.getFruit().getPricePerUnit().multiply(BigDecimal.valueOf(df.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return drinkFruitSum.add(cupPrice);
        } else {
            BigDecimal fruitSum = fruits.stream()
                    .map(f -> f.getFruit().getPricePerUnit().multiply(BigDecimal.valueOf(f.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return fruitSum.add(cupPrice);
        }
    }

    public BigDecimal getTotalPrice() {
        return getUnitPrice().multiply(BigDecimal.valueOf(quantity));
    }
}


