package project.mr_smoothy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_item_fruits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemFruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fruit_id")
    private Fruit fruit;

    @Column(nullable = false)
    private Integer quantity;
}

