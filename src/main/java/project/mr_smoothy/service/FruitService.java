package project.mr_smoothy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.mr_smoothy.dto.request.FruitCreateRequest;
import project.mr_smoothy.dto.request.FruitUpdateRequest;
import project.mr_smoothy.dto.response.FruitResponse;
import project.mr_smoothy.entity.Fruit;
import project.mr_smoothy.repository.FruitRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FruitService {

    private final FruitRepository fruitRepository;
    private final USDAService usdaService;
    private final USDADataParser usdaDataParser;
    private final OpenAIService openAIService;
    private final ObjectMapper objectMapper;
    
    @Value("${usda.api.key:}")
    private String usdaApiKey;

    public FruitResponse create(FruitCreateRequest request) {
        if (fruitRepository.existsByNameIgnoreCase(request.getName())) {
            throw new RuntimeException("Fruit name already exists");
        }
        Fruit fruit = new Fruit();
        fruit.setName(request.getName());
        fruit.setDescription(request.getDescription());
        fruit.setImageUrl(request.getImageUrl());
        fruit.setPricePerUnit(request.getPricePerUnit());
        fruit.setCategory(request.getCategory() != null ? request.getCategory() : Fruit.Category.FRUIT);
        fruit.setActive(request.getActive() != null ? request.getActive() : true);
        fruit.setSeasonal(request.getSeasonal() != null ? request.getSeasonal() : false);
        
        // Try to fetch nutrition data automatically (optional - won't fail if it doesn't work)
        // Only fetch if nutrition data is missing to save API tokens
        boolean hasNutritionData = fruit.getCalorie() != null || 
                                   fruit.getProtein() != null || 
                                   fruit.getFiber() != null;
        
        if (!hasNutritionData) {
            try {
                // Check if USDA API key is configured before attempting to fetch
                if (usdaApiKey == null || usdaApiKey.isEmpty() || 
                    usdaApiKey.equals("your_usda_api_key_here") || 
                    usdaApiKey.trim().isEmpty()) {
                    log.info("USDA API key not configured. Skipping nutrition data fetch for: {}. " +
                            "To enable automatic nutrition data fetching, set USDA_API_KEY in docker-compose.yaml", 
                            fruit.getName());
                } else {
                    fetchAndSetNutritionData(fruit);
                    log.info("Successfully fetched nutrition data for: {}", fruit.getName());
                }
            } catch (Exception e) {
                log.warn("Could not fetch nutrition data for {}: {}. Continuing without nutrition data.", 
                        fruit.getName(), e.getMessage());
                // Continue without nutrition data - not a critical error
            }
        } else {
            log.info("Nutrition data already exists for: {}. Skipping API call to save tokens.", fruit.getName());
        }
        
        Fruit saved = fruitRepository.save(fruit);
        return toResponse(saved);
    }
    
    /**
     * Fetch and set nutrition data for a fruit (internal helper method)
     * This method is called automatically when creating a fruit
     */
    private void fetchAndSetNutritionData(Fruit fruit) {
        try {
            // Step 1: Search USDA API
            JsonNode searchResponse = usdaService.searchFood(fruit.getName());
            Integer fdcId = usdaService.extractFdcId(searchResponse);
            
            if (fdcId == null) {
                log.warn("Could not find USDA data for: {}", fruit.getName());
                return;
            }

            // Step 2: Get detailed USDA data
            JsonNode usdaDetails = usdaService.getFoodDetails(fdcId);

            // Step 3: Parse nutrition data directly from USDA (no AI needed - saves tokens!)
            Map<String, Object> nutritionData = usdaDataParser.parseUSDAData(fruit.getName(), usdaDetails);

            // Step 4: Get flavor profile from OpenAI (optional, uses minimal tokens)
            Map<String, Object> flavorData = new HashMap<>();
            try {
                String flavorPrompt = openAIService.buildFlavorAnalysisPrompt(fruit.getName(), nutritionData);
                String flavorResponse = openAIService.callOpenAI(flavorPrompt);
                flavorData = openAIService.parseOpenAIResponse(flavorResponse);
            } catch (Exception e) {
                log.warn("Failed to generate flavor profile for {}: {}. Continuing without flavor data.", 
                        fruit.getName(), e.getMessage());
                // Continue without flavor data - not critical
            }

            // Step 5: Merge nutrition and flavor data
            Map<String, Object> processedData = new HashMap<>(nutritionData);
            processedData.putAll(flavorData);

            // Step 6: Set nutrition data
            setNutritionDataFromProcessed(fruit, processedData, usdaDetails);
            
        } catch (Exception e) {
            log.error("Error fetching nutrition data for {}: {}", fruit.getName(), e.getMessage(), e);
            throw e; // Re-throw to be caught by caller
        }
    }
    
    /**
     * Set nutrition data from processed OpenAI response
     */
    private void setNutritionDataFromProcessed(Fruit fruit, Map<String, Object> processedData, JsonNode usdaDetails) {
        if (processedData.containsKey("calorie")) {
            fruit.setCalorie(new BigDecimal(processedData.get("calorie").toString()));
        }
        if (processedData.containsKey("protein")) {
            fruit.setProtein(new BigDecimal(processedData.get("protein").toString()));
        }
        if (processedData.containsKey("fiber")) {
            fruit.setFiber(new BigDecimal(processedData.get("fiber").toString()));
        }
        try {
            if (processedData.containsKey("vitamins")) {
                fruit.setVitamins(objectMapper.writeValueAsString(processedData.get("vitamins")));
            }
            if (processedData.containsKey("minerals")) {
                fruit.setMinerals(objectMapper.writeValueAsString(processedData.get("minerals")));
            }
            if (processedData.containsKey("flavor_profile")) {
                fruit.setFlavorProfile(processedData.get("flavor_profile").toString());
            }
            if (processedData.containsKey("taste_notes")) {
                fruit.setTasteNotes(processedData.get("taste_notes").toString());
            }
            if (processedData.containsKey("best_mix_pairing")) {
                fruit.setBestMixPairing(objectMapper.writeValueAsString(processedData.get("best_mix_pairing")));
            }
            if (processedData.containsKey("avoid_pairing")) {
                fruit.setAvoidPairing(objectMapper.writeValueAsString(processedData.get("avoid_pairing")));
            }
        } catch (JsonProcessingException e) {
            log.error("Error converting processed data to JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process ingredient data: " + e.getMessage(), e);
        }

        // Store raw USDA data
        fruit.setRawUsdaData(usdaDetails.toString());
    }

    /**
     * Updates an existing fruit entity with the provided request data.
     * This method follows OOP principles by encapsulating the update logic
     * and ensuring all fields are properly updated including description.
     * 
     * @param id The ID of the fruit to update
     * @param request The update request containing new field values
     * @return FruitResponse containing the updated fruit data
     * @throws RuntimeException if fruit is not found
     */
    public FruitResponse update(Long id, FruitUpdateRequest request) {
        Fruit fruit = fruitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fruit not found"));
        
        updateFruitFields(fruit, request);
        
        Fruit savedFruit = fruitRepository.save(fruit);
        return toResponse(savedFruit);
    }
    
    /**
     * Updates the fruit entity fields based on the update request.
     * This method encapsulates the field update logic following OOP principles.
     * Description is always updated when provided in the request, including empty strings.
     * 
     * @param fruit The fruit entity to update
     * @param request The update request containing new field values
     */
    private void updateFruitFields(Fruit fruit, FruitUpdateRequest request) {
        if (request.getName() != null) {
            fruit.setName(request.getName());
        }
        
        // Description is always updated when provided in the request
        // Frontend always sends description field when updating
        // Empty string is a valid value and should be saved
        if (request.getDescription() != null) {
            fruit.setDescription(request.getDescription());
        }
        
        if (request.getImageUrl() != null) {
            fruit.setImageUrl(request.getImageUrl());
        }
        
        if (request.getPricePerUnit() != null) {
            fruit.setPricePerUnit(request.getPricePerUnit());
        }
        
        if (request.getCategory() != null) {
            fruit.setCategory(request.getCategory());
        }
        
        if (request.getActive() != null) {
            fruit.setActive(request.getActive());
        }
        
        if (request.getSeasonal() != null) {
            fruit.setSeasonal(request.getSeasonal());
        }
    }

    public void delete(Long id) {
        if (!fruitRepository.existsById(id)) throw new RuntimeException("Fruit not found");
        fruitRepository.deleteById(id);
    }

    public FruitResponse get(Long id) {
        return fruitRepository.findById(id).map(this::toResponse).orElseThrow(() -> new RuntimeException("Fruit not found"));
    }

    @Transactional(readOnly = true)
    public List<FruitResponse> list() {
        return fruitRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FruitResponse> listActive() {
        return fruitRepository.findByActiveTrue().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FruitResponse> listSeasonal() {
        return fruitRepository.findByActiveTrueAndSeasonalTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Bulk update seasonal status for multiple ingredients.
     * This method follows OOP principles by encapsulating the bulk update logic.
     * 
     * @param ingredientIds List of ingredient IDs to update
     * @param seasonal The seasonal status to set
     * @return List of updated FruitResponse
     */
    public List<FruitResponse> bulkUpdateSeasonal(List<Long> ingredientIds, Boolean seasonal) {
        List<Fruit> ingredients = fruitRepository.findAllById(ingredientIds);
        
        for (Fruit ingredient : ingredients) {
            ingredient.setSeasonal(seasonal);
        }
        
        List<Fruit> savedIngredients = fruitRepository.saveAll(ingredients);
        return savedIngredients.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private FruitResponse toResponse(Fruit f) {
        return FruitResponse.builder()
                .id(f.getId())
                .name(f.getName())
                .description(f.getDescription())
                .imageUrl(f.getImageUrl())
                .pricePerUnit(f.getPricePerUnit())
                .category(f.getCategory())
                .active(f.getActive())
                .seasonal(f.getSeasonal())
                .calorie(f.getCalorie())
                .protein(f.getProtein())
                .fiber(f.getFiber())
                .vitamins(f.getVitamins())
                .minerals(f.getMinerals())
                .flavorProfile(f.getFlavorProfile())
                .tasteNotes(f.getTasteNotes())
                .bestMixPairing(f.getBestMixPairing())
                .avoidPairing(f.getAvoidPairing())
                .build();
    }
}


