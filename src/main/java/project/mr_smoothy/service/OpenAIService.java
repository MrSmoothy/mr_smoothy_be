package project.mr_smoothy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for interacting with OpenAI API
 * Processes USDA data and generates nutrition/flavor information
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIService {

    private final ObjectMapper objectMapper;
    private final WebClient.Builder webClientBuilder;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.model:gpt-4-turbo-preview}")
    private String openaiModel;

    @Value("${openai.api.base-url:https://api.openai.com/v1}")
    private String openaiBaseUrl;

    /**
     * Process USDA data and generate structured nutrition/flavor information
     * @param ingredientName The name of the ingredient
     * @param usdaData The raw USDA JSON data
     * @return Structured JSON with nutrition and flavor data
     */
    public Map<String, Object> processIngredientData(String ingredientName, JsonNode usdaData) {
        try {
            log.info("Processing ingredient data with OpenAI for: {}", ingredientName);

            String prompt = buildIngredientProcessingPrompt(ingredientName, usdaData);
            String response = callOpenAI(prompt);

            // Parse OpenAI response
            Map<String, Object> result = parseOpenAIResponse(response);
            
            log.info("Successfully processed ingredient data for: {}", ingredientName);
            return result;

        } catch (Exception e) {
            log.error("Error processing ingredient data with OpenAI: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process ingredient data with OpenAI: " + e.getMessage(), e);
        }
    }

    /**
     * Analyze smoothy combination and generate flavor description
     * @param ingredients List of ingredients with amounts and nutrition data
     * @return Analysis result with flavor description, synergy, and cancellation
     */
    public Map<String, Object> analyzeSmoothyCombination(List<Map<String, Object>> ingredients) {
        try {
            log.info("Analyzing smoothy combination with {} ingredients", ingredients.size());

            String prompt = buildSmoothyAnalysisPrompt(ingredients);
            String response = callOpenAI(prompt);

            // Parse OpenAI response
            Map<String, Object> result = parseOpenAIResponse(response);
            
            log.info("Successfully analyzed smoothy combination");
            return result;

        } catch (Exception e) {
            log.error("Error analyzing smoothy combination: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to analyze smoothy combination: " + e.getMessage(), e);
        }
    }

    private String buildIngredientProcessingPrompt(String ingredientName, JsonNode usdaData) {
        return String.format(
            "You are a nutrition and flavor-analysis expert. Clean, normalize, and convert the USDA data " +
            "into structured JSON for database storage.\n\n" +
            "Ingredient Name: %s\n\n" +
            "USDA Data:\n%s\n\n" +
            "Please provide a JSON object with the following structure (return JSON only, no markdown):\n" +
            "{\n" +
            "  \"calorie\": <number>, // calories per 100g\n" +
            "  \"protein\": <number>, // grams per 100g\n" +
            "  \"fiber\": <number>, // grams per 100g\n" +
            "  \"vitamins\": {\"vitaminC\": <number>, \"vitaminA\": <number>, ...}, // all vitamins in mg or IU\n" +
            "  \"minerals\": {\"calcium\": <number>, \"iron\": <number>, ...}, // all minerals in mg\n" +
            "  \"flavor_profile\": \"<description>\", // e.g., \"sweet, tropical\"\n" +
            "  \"taste_notes\": \"<detailed description>\", // detailed taste description\n" +
            "  \"best_mix_pairing\": [\"<ingredient1>\", \"<ingredient2>\", ...], // array of ingredient names\n" +
            "  \"avoid_pairing\": [\"<ingredient1>\", \"<ingredient2>\", ...] // array of ingredients to avoid\n" +
            "}\n\n" +
            "Extract all available nutrition data from the USDA data. If any data is missing, use reasonable defaults based on the ingredient type.",
            ingredientName,
            usdaData.toString()
        );
    }

    private String buildSmoothyAnalysisPrompt(List<Map<String, Object>> ingredients) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a smoothy flavor and nutrition expert. Analyze the following smoothy combination:\n\n");
        
        for (Map<String, Object> ingredient : ingredients) {
            prompt.append(String.format(
                "- %s: %.2fg (Calories: %.2f, Protein: %.2fg, Fiber: %.2fg, Flavor: %s)\n",
                ingredient.get("name"),
                ingredient.get("amount"),
                ingredient.get("calorie"),
                ingredient.get("protein"),
                ingredient.get("fiber"),
                ingredient.get("flavorProfile")
            ));
        }
        
        prompt.append("\nPlease provide a JSON object with the following structure (return JSON only, no markdown):\n");
        prompt.append("{\n");
        prompt.append("  \"flavorDescription\": \"<user-friendly description of the smoothy>\",\n");
        prompt.append("  \"synergy\": [\"<positive interaction 1>\", \"<positive interaction 2>\", ...],\n");
        prompt.append("  \"cancellation\": [\"<negative interaction 1>\", \"<negative interaction 2>\", ...]\n");
        prompt.append("}\n\n");
        prompt.append("Analyze flavor outcome, nutrient synergy, and any cancellation effects. Provide a friendly, engaging description.");

        return prompt.toString();
    }

    /**
     * Call OpenAI API (public method for use by other services)
     */
    public String callOpenAI(String prompt) {
        try {
            log.info("Calling OpenAI API with model: {}", openaiModel);
            
            WebClient webClient = webClientBuilder
                    .baseUrl(openaiBaseUrl)
                    .defaultHeader("Authorization", "Bearer " + openaiApiKey)
                    .defaultHeader("Content-Type", "application/json")
                    .build();

            // Build request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", openaiModel);
            requestBody.put("messages", List.of(
                    Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 2000);

            String response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();

            if (response == null || response.isEmpty()) {
                throw new RuntimeException("OpenAI API returned empty response");
            }

            // Parse response
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode choices = jsonResponse.get("choices");
            
            if (choices == null || !choices.isArray() || choices.size() == 0) {
                throw new RuntimeException("OpenAI API returned no choices");
            }

            String content = choices.get(0).get("message").get("content").asText();
            
            // Extract JSON from markdown code blocks if present
            if (content.contains("```json")) {
                int start = content.indexOf("```json") + 7;
                int end = content.indexOf("```", start);
                if (end > start) {
                    content = content.substring(start, end).trim();
                }
            } else if (content.contains("```")) {
                int start = content.indexOf("```") + 3;
                int end = content.indexOf("```", start);
                if (end > start) {
                    content = content.substring(start, end).trim();
                }
            }

            return content.trim();

        } catch (Exception e) {
            log.error("Error calling OpenAI API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to call OpenAI API: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseOpenAIResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return (Map<String, Object>) objectMapper.convertValue(jsonNode, Map.class);
        } catch (Exception e) {
            log.error("Error parsing OpenAI response: {}", e.getMessage(), e);
            log.error("Response was: {}", response);
            throw new RuntimeException("Failed to parse OpenAI response: " + e.getMessage(), e);
        }
    }
}

