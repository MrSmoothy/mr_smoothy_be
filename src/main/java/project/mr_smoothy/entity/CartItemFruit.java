package project.mr_smoothy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_item_fruits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemFruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cart_item_id")
    private CartItem cartItem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ingredient_id")
    private Fruit fruit;

    @Column(nullable = false)
    private Integer quantity; // จำนวนหน่วยผลไม้ในแก้ว (สำหรับ custom)
}


