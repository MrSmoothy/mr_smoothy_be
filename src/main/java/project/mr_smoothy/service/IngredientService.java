package project.mr_smoothy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.mr_smoothy.dto.request.IngredientAddRequest;
import project.mr_smoothy.dto.response.IngredientAddResponse;
import project.mr_smoothy.entity.Fruit;
import project.mr_smoothy.repository.FruitRepository;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Service for managing ingredients with USDA and OpenAI integration
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class IngredientService {

    private final FruitRepository fruitRepository;
    private final USDAService usdaService;
    private final OpenAIService openAIService;
    private final ObjectMapper objectMapper;

    /**
     * Add ingredient with USDA and OpenAI processing
     */
    public IngredientAddResponse addIngredientWithNutrition(IngredientAddRequest request) {
        log.info("Adding ingredient with nutrition data: {}", request.getName());

        // Check if ingredient already exists
        if (fruitRepository.existsByNameIgnoreCase(request.getName())) {
            throw new RuntimeException("Ingredient with name '" + request.getName() + "' already exists");
        }

        // Step 1: Search USDA API
        JsonNode searchResponse = usdaService.searchFood(request.getName());
        Integer fdcId = usdaService.extractFdcId(searchResponse);
        
        if (fdcId == null) {
            throw new RuntimeException("Could not find USDA data for: " + request.getName());
        }

        // Step 2: Get detailed USDA data
        JsonNode usdaDetails = usdaService.getFoodDetails(fdcId);

        // Step 3: Process with OpenAI
        Map<String, Object> processedData = openAIService.processIngredientData(
                request.getName(), 
                usdaDetails
        );

        // Step 4: Create and save ingredient
        Fruit ingredient = new Fruit();
        ingredient.setName(request.getName());
        ingredient.setDescription(request.getDescription());
        ingredient.setImageUrl(request.getImageUrl());
        ingredient.setPricePerUnit(request.getPricePerUnit() != null 
                ? request.getPricePerUnit() 
                : BigDecimal.ZERO);
        ingredient.setCategory(request.getCategory() != null 
                ? request.getCategory() 
                : Fruit.Category.FRUIT);
        ingredient.setActive(request.getActive() != null ? request.getActive() : true);
        ingredient.setSeasonal(request.getSeasonal() != null ? request.getSeasonal() : false);

        // Set nutrition data from OpenAI response
        setNutritionData(ingredient, processedData, usdaDetails);

        Fruit saved = fruitRepository.save(ingredient);

        // Convert to response
        return toIngredientAddResponse(saved);
    }

    /**
     * Fetch and update nutrition data for existing ingredient
     */
    public IngredientAddResponse fetchAndUpdateNutrition(Long ingredientId) {
        log.info("Fetching nutrition data for ingredient ID: {}", ingredientId);

        Fruit ingredient = fruitRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + ingredientId));

        try {
            // Step 1: Search USDA API
            JsonNode searchResponse = usdaService.searchFood(ingredient.getName());
            Integer fdcId = usdaService.extractFdcId(searchResponse);
            
            if (fdcId == null) {
                throw new RuntimeException("Could not find USDA data for: " + ingredient.getName() + 
                        ". Please make sure the ingredient name is in English.");
            }

            // Step 2: Get detailed USDA data
            JsonNode usdaDetails = usdaService.getFoodDetails(fdcId);

            // Step 3: Process with OpenAI
            Map<String, Object> processedData = openAIService.processIngredientData(
                    ingredient.getName(), 
                    usdaDetails
            );

            // Step 4: Update nutrition data
            setNutritionData(ingredient, processedData, usdaDetails);

            Fruit saved = fruitRepository.save(ingredient);

            return toIngredientAddResponse(saved);
        } catch (RuntimeException e) {
            // Re-throw with more helpful message
            if (e.getMessage().contains("USDA API key") || e.getMessage().contains("403")) {
                throw new RuntimeException(
                    "ไม่สามารถดึงข้อมูลโภชนาการได้: " + e.getMessage() + 
                    " กรุณาตรวจสอบ USDA API key ใน docker-compose.yaml หรือ application.properties"
                );
            }
            throw e;
        }
    }

    /**
     * Set nutrition data from processed OpenAI response
     */
    private void setNutritionData(Fruit ingredient, Map<String, Object> processedData, JsonNode usdaDetails) {
        if (processedData.containsKey("calorie")) {
            ingredient.setCalorie(new BigDecimal(processedData.get("calorie").toString()));
        }
        if (processedData.containsKey("protein")) {
            ingredient.setProtein(new BigDecimal(processedData.get("protein").toString()));
        }
        if (processedData.containsKey("fiber")) {
            ingredient.setFiber(new BigDecimal(processedData.get("fiber").toString()));
        }
        try {
            if (processedData.containsKey("vitamins")) {
                ingredient.setVitamins(objectMapper.writeValueAsString(processedData.get("vitamins")));
            }
            if (processedData.containsKey("minerals")) {
                ingredient.setMinerals(objectMapper.writeValueAsString(processedData.get("minerals")));
            }
            if (processedData.containsKey("flavor_profile")) {
                ingredient.setFlavorProfile(processedData.get("flavor_profile").toString());
            }
            if (processedData.containsKey("taste_notes")) {
                ingredient.setTasteNotes(processedData.get("taste_notes").toString());
            }
            if (processedData.containsKey("best_mix_pairing")) {
                ingredient.setBestMixPairing(objectMapper.writeValueAsString(processedData.get("best_mix_pairing")));
            }
            if (processedData.containsKey("avoid_pairing")) {
                ingredient.setAvoidPairing(objectMapper.writeValueAsString(processedData.get("avoid_pairing")));
            }
        } catch (JsonProcessingException e) {
            log.error("Error converting processed data to JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process ingredient data: " + e.getMessage(), e);
        }

        // Store raw USDA data
        ingredient.setRawUsdaData(usdaDetails.toString());
    }

    /**
     * Convert Fruit entity to IngredientAddResponse
     */
    private IngredientAddResponse toIngredientAddResponse(Fruit saved) {
        return IngredientAddResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .imageUrl(saved.getImageUrl())
                .pricePerUnit(saved.getPricePerUnit())
                .category(saved.getCategory())
                .active(saved.getActive())
                .seasonal(saved.getSeasonal())
                .calorie(saved.getCalorie())
                .protein(saved.getProtein())
                .fiber(saved.getFiber())
                .vitamins(saved.getVitamins())
                .minerals(saved.getMinerals())
                .flavorProfile(saved.getFlavorProfile())
                .tasteNotes(saved.getTasteNotes())
                .bestMixPairing(saved.getBestMixPairing())
                .avoidPairing(saved.getAvoidPairing())
                .build();
    }
}
