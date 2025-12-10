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
import java.util.HashMap;
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
    private final USDADataParser usdaDataParser;
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

        // Step 1: REQUIRED - Fetch nutrition data from USDA (must succeed)
        // Note: USDAService will automatically translate Thai names to English
        Map<String, Object> processedData = new HashMap<>();
        JsonNode usdaDetails = null;
        
        try {
            JsonNode searchResponse = usdaService.searchFood(request.getName());
            Integer fdcId = usdaService.extractFdcId(searchResponse);
            
            if (fdcId == null) {
                throw new RuntimeException(
                    "ไม่พบข้อมูลโภชนาการสำหรับ: " + request.getName() + 
                    " ใน USDA database.\n\n" +
                    "กรุณาตรวจสอบ:\n" +
                    "1. ตรวจสอบว่า USDA_API_KEY ถูกตั้งค่าใน docker-compose.yaml\n" +
                    "2. ชื่อวัตถุดิบถูกต้อง (ระบบจะแปลชื่อไทยเป็นอังกฤษให้อัตโนมัติ)\n" +
                    "3. ลองใช้ชื่ออังกฤษตรงๆ เช่น 'Grape', 'Banana', 'Strawberry'\n\n" +
                    "หรือแก้ไขวัตถุดิบนี้ภายหลังและกดปุ่ม 'ดึงข้อมูลโภชนาการ' เพื่อลองอีกครั้ง"
                );
            }

            // Step 2: Get detailed USDA data
            usdaDetails = usdaService.getFoodDetails(fdcId);

            // Step 3: Parse nutrition data directly from USDA (no AI needed - saves tokens!)
            Map<String, Object> nutritionData = usdaDataParser.parseUSDAData(request.getName(), usdaDetails);
            processedData.putAll(nutritionData);

            // Validate that we got essential nutrition data
            if (!processedData.containsKey("calorie") || processedData.get("calorie") == null) {
                throw new RuntimeException(
                    "ไม่สามารถดึงข้อมูลโภชนาการพื้นฐาน (แคลอรี่) สำหรับ: " + request.getName() + 
                    " กรุณาลองใหม่อีกครั้งหรือแก้ไขภายหลัง"
                );
            }

            // Step 4: Get flavor profile and pairing suggestions from OpenAI (optional, uses minimal tokens)
            Map<String, Object> flavorData = new HashMap<>();
            try {
                String flavorPrompt = openAIService.buildFlavorAnalysisPrompt(request.getName(), nutritionData);
                String flavorResponse = openAIService.callOpenAI(flavorPrompt);
                flavorData = openAIService.parseOpenAIResponse(flavorResponse);
                log.info("Successfully generated flavor profile for: {}", request.getName());
            } catch (Exception e) {
                log.warn("Failed to generate flavor profile for {}: {}. Continuing without flavor data.", 
                        request.getName(), e.getMessage());
                // Continue without flavor data - not critical
            }

            // Step 5: Merge nutrition and flavor data
            processedData.putAll(flavorData);
        } catch (RuntimeException e) {
            // Re-throw with helpful message
            throw e;
        } catch (Exception e) {
            log.error("Error fetching nutrition data for {}: {}", request.getName(), e.getMessage(), e);
            throw new RuntimeException(
                "ไม่สามารถดึงข้อมูลโภชนาการสำหรับ: " + request.getName() + 
                " เนื่องจาก: " + e.getMessage() +
                "\nกรุณาตรวจสอบ USDA_API_KEY ใน docker-compose.yaml หรือแก้ไขวัตถุดิบนี้ภายหลังเพื่อดึงข้อมูลโภชนาการ",
                e
            );
        }

        // Step 6: Create and save ingredient
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

        // Set nutrition data (from parser) and flavor data (from OpenAI)
        // This should always have data since we throw error if fetch fails
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
                throw new RuntimeException(
                    "ไม่พบข้อมูลโภชนาการสำหรับ: " + ingredient.getName() + 
                    " ใน USDA database. กรุณาตรวจสอบว่า:" +
                    "\n1. ชื่อวัตถุดิบเป็นภาษาอังกฤษ (เช่น 'Grape' แทน 'องุ่น')" +
                    "\n2. USDA_API_KEY ถูกตั้งค่าใน docker-compose.yaml" +
                    "\n3. ชื่อวัตถุดิบถูกต้อง"
                );
            }

            // Step 2: Get detailed USDA data
            JsonNode usdaDetails = usdaService.getFoodDetails(fdcId);

            // Step 3: Parse nutrition data directly from USDA (no AI needed - saves tokens!)
            Map<String, Object> nutritionData = usdaDataParser.parseUSDAData(ingredient.getName(), usdaDetails);

            // Validate that we got essential nutrition data
            if (!nutritionData.containsKey("calorie") || nutritionData.get("calorie") == null) {
                throw new RuntimeException(
                    "ไม่สามารถดึงข้อมูลโภชนาการพื้นฐาน (แคลอรี่) สำหรับ: " + ingredient.getName()
                );
            }

            // Step 4: Get flavor profile and pairing suggestions from OpenAI (optional, uses minimal tokens)
            Map<String, Object> flavorData = new HashMap<>();
            try {
                String flavorPrompt = openAIService.buildFlavorAnalysisPrompt(ingredient.getName(), nutritionData);
                String flavorResponse = openAIService.callOpenAI(flavorPrompt);
                flavorData = openAIService.parseOpenAIResponse(flavorResponse);
                log.info("Successfully generated flavor profile for: {}", ingredient.getName());
            } catch (Exception e) {
                log.warn("Failed to generate flavor profile for {}: {}. Continuing without flavor data.", 
                        ingredient.getName(), e.getMessage());
                // Continue without flavor data - not critical
            }

            // Step 5: Merge nutrition and flavor data
            Map<String, Object> processedData = new HashMap<>(nutritionData);
            processedData.putAll(flavorData);

            // Step 6: Update nutrition data
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

        // Store raw USDA data if available
        if (usdaDetails != null) {
            ingredient.setRawUsdaData(usdaDetails.toString());
        }
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
