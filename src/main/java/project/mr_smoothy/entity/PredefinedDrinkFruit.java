package project.mr_smoothy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "predefined_drink_fruits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredefinedDrinkFruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "drink_id")
    private PredefinedDrink drink;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fruit_id")
    private Fruit fruit;

    @Column(nullable = false)
    private Integer quantity; // จำนวนหน่วยผลไม้ในเมนู
}


