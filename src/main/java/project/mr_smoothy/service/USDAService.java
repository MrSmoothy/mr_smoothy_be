package project.mr_smoothy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Service for interacting with USDA FoodData Central API
 * Documentation: https://fdc.nal.usda.gov/api-guide.html
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class USDAService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    private final OpenAIService openAIService;

    @Value("${usda.api.key:}")
    private String usdaApiKey;

    @Value("${usda.api.base-url:https://api.nal.usda.gov/fdc/v1}")
    private String usdaBaseUrl;

    /**
     * Translate food name to English using OpenAI
     */
    private String translateWithOpenAI(String foodName) {
        try {
            String prompt = String.format(
                "Translate the following food/ingredient name to English. " +
                "Return ONLY the English name, nothing else. " +
                "If it's already in English, return it as is.\n\n" +
                "Name: %s\n\n" +
                "English name:",
                foodName
            );
            
            String response = openAIService.callOpenAI(prompt);
            return response.trim();
        } catch (Exception e) {
            log.error("Error translating with OpenAI: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Translate food name to English if needed
     * This is a simple mapping for common Thai fruits/vegetables
     * Falls back to OpenAI translation if not found in mapping
     */
    private String translateToEnglish(String foodName) {
        // Common Thai to English mappings
        String lowerName = foodName.toLowerCase().trim();
        
        // Fruit mappings
        if (lowerName.contains("กล้วย") || lowerName.equals("banana")) return "banana";
        if (lowerName.contains("สตรอเบอรี่") || lowerName.contains("สตรอเบอร์รี่") || lowerName.equals("strawberry")) return "strawberry";
        if (lowerName.contains("เลม่อน") || lowerName.equals("lemon")) return "lemon";
        if (lowerName.contains("บลูเบอรี่") || lowerName.contains("บลูเบอร์รี่") || lowerName.equals("blueberry")) return "blueberry";
        if (lowerName.contains("ส้ม") || lowerName.equals("orange")) return "orange";
        if (lowerName.contains("แอปเปิล") || lowerName.contains("แอปเปิ้ล") || lowerName.equals("apple")) return "apple";
        if (lowerName.contains("มะนาว") || lowerName.equals("lime")) return "lime";
        if (lowerName.contains("องุ่น") || lowerName.equals("grape")) return "grape";
        if (lowerName.contains("กีวี") || lowerName.equals("kiwi")) return "kiwi";
        if (lowerName.contains("มะม่วง") || lowerName.equals("mango")) return "mango";
        if (lowerName.contains("สับปะรด") || lowerName.equals("pineapple")) return "pineapple";
        if (lowerName.contains("แตงโม") || lowerName.equals("watermelon")) return "watermelon";
        if (lowerName.contains("ลิ้นจี่") || lowerName.equals("lychee")) return "lychee";
        if (lowerName.contains("ลำไย") || lowerName.equals("longan")) return "longan";
        if (lowerName.contains("ทุเรียน") || lowerName.equals("durian")) return "durian";
        if (lowerName.contains("มังคุด") || lowerName.equals("mangosteen")) return "mangosteen";
        if (lowerName.contains("เงาะ") || lowerName.equals("rambutan")) return "rambutan";
        if (lowerName.contains("ลิ้นจี่") || lowerName.equals("lychee")) return "lychee";
        
        // Vegetable mappings
        if (lowerName.contains("ผักโขม") || lowerName.equals("spinach")) return "spinach";
        if (lowerName.contains("ผักแคล") || lowerName.contains("ผักคะน้า") || lowerName.equals("kale")) return "kale";
        if (lowerName.contains("แครอท") || lowerName.contains("แครอต") || lowerName.equals("carrot")) return "carrot";
        if (lowerName.contains("บีทรูท") || lowerName.equals("beetroot")) return "beetroot";
        if (lowerName.contains("แตงกวา") || lowerName.equals("cucumber")) return "cucumber";
        if (lowerName.contains("บร็อคโคลี่") || lowerName.contains("บร็อคโคลี") || 
            lowerName.contains("บล็อคโคลี่") || lowerName.contains("บล็อคโคลี") || 
            lowerName.equals("broccoli")) return "broccoli";
        if (lowerName.contains("กะหล่ำ") || lowerName.equals("cabbage")) return "cabbage";
        if (lowerName.contains("ผักกาด") || lowerName.equals("lettuce")) return "lettuce";
        if (lowerName.contains("มะเขือ") || lowerName.equals("eggplant")) return "eggplant";
        if (lowerName.contains("พริก") || lowerName.equals("pepper")) return "pepper";
        if (lowerName.contains("ขิง") || lowerName.equals("ginger")) return "ginger";
        if (lowerName.contains("กระเทียม") || lowerName.equals("garlic")) return "garlic";
        if (lowerName.contains("หอม") || lowerName.equals("onion")) return "onion";
        
        // Addon mappings
        if (lowerName.contains("น้ำผึ้ง") || lowerName.equals("honey")) return "honey";
        if (lowerName.contains("โยเกิร์ต") || lowerName.equals("yogurt")) return "yogurt";
        if (lowerName.contains("นม") || lowerName.equals("milk")) return "milk";
        if (lowerName.contains("อโวคาโด") || lowerName.contains("อโวคาโด้") || lowerName.equals("avocado")) return "avocado";
        if (lowerName.contains("เมล็ดเจีย") || lowerName.equals("chia seeds")) return "chia seeds";
        if (lowerName.contains("อัลมอนด์") || lowerName.contains("อัลม่อนด์") || lowerName.equals("almond")) return "almond";
        if (lowerName.contains("เวย์") || lowerName.contains("whey")) return "whey protein";
        
        // If already in English or not found in mapping, return as is
        // Check if it contains only English characters
        if (foodName.matches("^[a-zA-Z0-9\\s]+$")) {
            return foodName;
        }
        
        // If Thai name not found in mapping, use OpenAI to translate
        log.info("Could not find mapping for '{}'. Using OpenAI to translate to English.", foodName);
        try {
            String englishName = translateWithOpenAI(foodName);
            if (englishName != null && !englishName.isEmpty()) {
                log.info("OpenAI translated '{}' to '{}'", foodName, englishName);
                return englishName;
            }
        } catch (Exception e) {
            log.warn("Failed to translate '{}' with OpenAI: {}. Using original name.", foodName, e.getMessage());
        }
        
        // Fallback: return original name
        return foodName;
    }

    /**
     * Search for food items by name
     * @param foodName The name of the food to search for (can be Thai or English)
     * @return JSON response containing search results with fdcId
     */
    public JsonNode searchFood(String foodName) {
        try {
            // Check if API key is configured
            if (usdaApiKey == null || usdaApiKey.isEmpty() || 
                usdaApiKey.equals("your_usda_api_key_here") || 
                usdaApiKey.trim().isEmpty()) {
                throw new RuntimeException(
                    "USDA API key is not configured. " +
                    "Please set USDA_API_KEY in docker-compose.yaml or application.properties. " +
                    "Get your API key at: https://fdc.nal.usda.gov/api-guide.html");
            }
            
            // Translate to English if needed
            String englishName = translateToEnglish(foodName);
            log.info("Searching USDA API for: {} (translated to: {})", foodName, englishName);
            
            WebClient webClient = webClientBuilder
                    .baseUrl(usdaBaseUrl)
                    .build();

            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/foods/search")
                            .queryParam("query", englishName)
                            .queryParam("api_key", usdaApiKey)
                            .queryParam("pageSize", 1)
                            .build())
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                            clientResponse -> {
                                if (clientResponse.statusCode().value() == 403) {
                                    log.error("USDA API 403 Forbidden - Invalid or missing API key. Please check USDA_API_KEY in application.properties");
                                    return Mono.error(new RuntimeException(
                                            "USDA API access denied (403). Please check your USDA API key. " +
                                            "Get your API key at: https://fdc.nal.usda.gov/api-guide.html"));
                                }
                                return Mono.error(new RuntimeException(
                                        "USDA API error: " + clientResponse.statusCode()));
                            })
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            if (response == null || response.isEmpty()) {
                throw new RuntimeException("USDA API returned empty response");
            }

            JsonNode jsonResponse = objectMapper.readTree(response);
            
            // Check if we have results
            JsonNode foods = jsonResponse.get("foods");
            if (foods == null || !foods.isArray() || foods.size() == 0) {
                throw new RuntimeException("No food found for: " + foodName + 
                        " (searched as: " + englishName + "). " +
                        "Please make sure the ingredient name is correct or try using English name.");
            }

            log.info("Found {} results for {} (searched as: {})", foods.size(), foodName, englishName);
            return jsonResponse;

        } catch (Exception e) {
            log.error("Error searching USDA API for {}: {}", foodName, e.getMessage(), e);
            throw new RuntimeException("Failed to search USDA API: " + e.getMessage(), e);
        }
    }

    /**
     * Get detailed food information by fdcId
     * @param fdcId The FoodData Central ID
     * @return JSON response containing detailed nutrient information
     */
    public JsonNode getFoodDetails(Integer fdcId) {
        try {
            log.info("Fetching USDA food details for fdcId: {}", fdcId);
            
            WebClient webClient = webClientBuilder
                    .baseUrl(usdaBaseUrl)
                    .build();

            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/food/{fdcId}")
                            .queryParam("api_key", usdaApiKey)
                            .build(fdcId))
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            if (response == null || response.isEmpty()) {
                throw new RuntimeException("USDA API returned empty response for fdcId: " + fdcId);
            }

            JsonNode jsonResponse = objectMapper.readTree(response);
            log.info("Successfully fetched food details for fdcId: {}", fdcId);
            return jsonResponse;

        } catch (Exception e) {
            log.error("Error fetching USDA food details for fdcId {}: {}", fdcId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch USDA food details: " + e.getMessage(), e);
        }
    }

    /**
     * Get fdcId from search results
     * @param searchResponse The search response JSON
     * @return The first fdcId found, or null if not found
     */
    public Integer extractFdcId(JsonNode searchResponse) {
        try {
            JsonNode foods = searchResponse.get("foods");
            if (foods != null && foods.isArray() && foods.size() > 0) {
                JsonNode firstFood = foods.get(0);
                if (firstFood.has("fdcId")) {
                    return firstFood.get("fdcId").asInt();
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Error extracting fdcId: {}", e.getMessage(), e);
            return null;
        }
    }
}

