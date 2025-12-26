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
        ORGANIC_FRUITS,      // ผลไม้ออร์แกนิก
        ORGANIC_VEGETABLE,   // ผักออร์แกนิก
        BASE,                // ฐาน
        SUPERFRUITS,         // ซูเปอร์ฟรุต
        PROTEIN,             // โปรตีน
        TOPPING,             // ท็อปปิ้ง
        SWEETENER            // สารให้ความหวาน
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
    @Column(nullable = false, length = 50)
    private Category category = Category.ORGANIC_FRUITS;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean seasonal = false; // วัตถุดิบตามฤดูกาล

    // Nutrition Data (from USDA + OpenAI)
    @Column(name = "calorie", precision = 10, scale = 2)
    private BigDecimal calorie; // calories per 100g

    @Column(name = "protein", precision = 10, scale = 2)
    private BigDecimal protein; // grams per 100g

    @Column(name = "fiber", precision = 10, scale = 2)
    private BigDecimal fiber; // grams per 100g

    @Column(name = "vitamins", columnDefinition = "JSON")
    private String vitamins; // JSON object: {"vitaminC": 50, "vitaminA": 100, ...}

    @Column(name = "minerals", columnDefinition = "JSON")
    private String minerals; // JSON object: {"calcium": 20, "iron": 1.5, ...}

    // Flavor & Pairing Data (from OpenAI)
    @Column(name = "flavor_profile", length = 255)
    private String flavorProfile; // e.g., "sweet, tropical"

    @Column(name = "taste_notes", columnDefinition = "TEXT")
    private String tasteNotes; // detailed taste description

    @Column(name = "best_mix_pairing", columnDefinition = "JSON")
    private String bestMixPairing; // JSON array: ["banana", "strawberry", ...]

    @Column(name = "avoid_pairing", columnDefinition = "JSON")
    private String avoidPairing; // JSON array: ["dairy", "citrus", ...]

    // Raw USDA Data (for reference)
    @Column(name = "raw_usda_data", columnDefinition = "LONGTEXT")
    private String rawUsdaData; // Complete USDA API response as JSON string
}


