package project.mr_smoothy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.mr_smoothy.dto.request.SmoothyCalcRequest;
import project.mr_smoothy.dto.response.SmoothyCalcResponse;
import project.mr_smoothy.entity.Fruit;
import project.mr_smoothy.repository.FruitRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for calculating smoothy nutrition and flavor analysis
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmoothyService {

    private final FruitRepository fruitRepository;
    private final OpenAIService openAIService;

    /**
     * Calculate smoothy nutrition and analyze flavor
     */
    public SmoothyCalcResponse calculateSmoothy(SmoothyCalcRequest request) {
        log.info("Calculating smoothy with {} ingredients", request.getIngredients().size());

        // Step 1: Fetch ingredients from database
        List<Map<String, Object>> ingredientData = new ArrayList<>();
        BigDecimal totalCalorie = BigDecimal.ZERO;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalFiber = BigDecimal.ZERO;

        for (SmoothyCalcRequest.IngredientAmount item : request.getIngredients()) {
            Fruit ingredient = fruitRepository.findById(item.getIngredientId())
                    .orElseThrow(() -> new RuntimeException(
                            "Ingredient not found with id: " + item.getIngredientId()));

            if (ingredient.getCalorie() == null || ingredient.getProtein() == null || 
                ingredient.getFiber() == null) {
                log.warn("Ingredient {} has missing nutrition data, skipping", ingredient.getName());
                continue;
            }

            // Calculate nutrition for the amount (assuming nutrition is per 100g)
            double amountInGrams = item.getAmount();
            double multiplier = amountInGrams / 100.0;

            BigDecimal calorie = ingredient.getCalorie().multiply(BigDecimal.valueOf(multiplier));
            BigDecimal protein = ingredient.getProtein().multiply(BigDecimal.valueOf(multiplier));
            BigDecimal fiber = ingredient.getFiber().multiply(BigDecimal.valueOf(multiplier));

            totalCalorie = totalCalorie.add(calorie);
            totalProtein = totalProtein.add(protein);
            totalFiber = totalFiber.add(fiber);

            // Prepare data for OpenAI
            Map<String, Object> ingredientMap = Map.of(
                    "name", ingredient.getName(),
                    "amount", amountInGrams,
                    "calorie", calorie.doubleValue(),
                    "protein", protein.doubleValue(),
                    "fiber", fiber.doubleValue(),
                    "flavorProfile", ingredient.getFlavorProfile() != null 
                            ? ingredient.getFlavorProfile() 
                            : "unknown"
            );
            ingredientData.add(ingredientMap);
        }

        // Step 2: Analyze with OpenAI
        Map<String, Object> analysis = openAIService.analyzeSmoothyCombination(ingredientData);

        // Step 3: Build response
        SmoothyCalcResponse.TotalNutrition totalNutrition = SmoothyCalcResponse.TotalNutrition.builder()
                .totalCalorie(totalCalorie)
                .totalProtein(totalProtein)
                .totalFiber(totalFiber)
                .build();

        @SuppressWarnings("unchecked")
        List<String> synergy = analysis.containsKey("synergy") && analysis.get("synergy") instanceof List
                ? (List<String>) analysis.get("synergy")
                : new ArrayList<>();
        
        @SuppressWarnings("unchecked")
        List<String> cancellation = analysis.containsKey("cancellation") && analysis.get("cancellation") instanceof List
                ? (List<String>) analysis.get("cancellation")
                : new ArrayList<>();

        return SmoothyCalcResponse.builder()
                .totalNutrition(totalNutrition)
                .flavorDescription(analysis.getOrDefault("flavorDescription", "").toString())
                .synergy(synergy)
                .cancellation(cancellation)
                .build();
    }
}

