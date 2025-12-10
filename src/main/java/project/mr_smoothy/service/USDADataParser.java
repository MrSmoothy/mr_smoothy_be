package project.mr_smoothy.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Parser for USDA FoodData Central API response
 * Extracts nutrition data directly from JSON without using AI
 * This reduces API costs significantly
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class USDADataParser {

    /**
     * Parse USDA data and extract nutrition information
     * This method directly extracts data from USDA JSON without AI processing
     * 
     * @param ingredientName The name of the ingredient
     * @param usdaData The USDA JSON response
     * @return Map containing parsed nutrition data
     */
    public Map<String, Object> parseUSDAData(String ingredientName, JsonNode usdaData) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // USDA data structure:
            // - foodNutrients: array of nutrient objects
            //   Each nutrient has: nutrient (with id, name), amount, unitName
            // - servingSizeUnit: unit (e.g., "g", "ml")
            // - servingSize: size (may not always be present)
            
            JsonNode foodNutrients = usdaData.get("foodNutrients");
            if (foodNutrients == null || !foodNutrients.isArray()) {
                log.warn("No foodNutrients found in USDA data for: {}", ingredientName);
                return result;
            }

            // USDA Nutrient IDs (from FDC documentation):
            // Reference: https://fdc.nal.usda.gov/api-guide.html
            // Energy & Macronutrients
            // 1008 = Energy (kcal)
            // 1003 = Protein
            // 1004 = Fat, total
            // 1005 = Carbohydrates
            // 1079 = Fiber, total dietary
            // Vitamins
            // 1106 = Vitamin A, RAE
            // 1104 = Vitamin A, IU
            // 1162 = Vitamin C (Ascorbic acid)
            // 1109 = Vitamin E (alpha-tocopherol)
            // 1110 = Vitamin K (phylloquinone)
            // 1165 = Thiamin (Vitamin B1)
            // 1166 = Riboflavin (Vitamin B2)
            // 1167 = Niacin (Vitamin B3)
            // 1175 = Vitamin B6
            // 1177 = Folate, total
            // 1178 = Vitamin B12
            // Minerals
            // 1087 = Calcium, Ca
            // 1089 = Iron, Fe
            // 1092 = Potassium, K
            // 1093 = Sodium, Na
            // 1095 = Zinc, Zn
            // 1098 = Magnesium, Mg
            // 1099 = Phosphorus, P
            // 1100 = Copper, Cu
            // 1101 = Manganese, Mn
            // 1103 = Selenium, Se
            
            Map<String, Double> vitamins = new HashMap<>();
            Map<String, Double> minerals = new HashMap<>();
            BigDecimal calorie = null;
            BigDecimal protein = null;
            BigDecimal fiber = null;

            // USDA data is typically per 100g or per serving
            // Check if we need to normalize - USDA usually provides per 100g already
            // But we check for servingSize and normalize if needed
            double servingSize = 100.0;
            String servingUnit = "g";
            
            if (usdaData.has("servingSize")) {
                servingSize = usdaData.get("servingSize").asDouble(100.0);
            }
            if (usdaData.has("servingSizeUnit")) {
                servingUnit = usdaData.get("servingSizeUnit").asText().toLowerCase();
            }
            
            // If serving size is not 100g, we need to normalize
            boolean needsNormalization = servingSize > 0 && servingSize != 100.0 && servingUnit.equals("g");

            // Parse all nutrients
            for (JsonNode nutrient : foodNutrients) {
                if (!nutrient.has("nutrient") || !nutrient.has("amount")) {
                    continue;
                }
                
                JsonNode nutrientInfo = nutrient.get("nutrient");
                if (!nutrientInfo.has("id")) {
                    continue;
                }
                
                int nutrientId = nutrientInfo.get("id").asInt();
                double amount = nutrient.get("amount").asDouble(0.0);
                
                // Normalize to per 100g if needed
                if (needsNormalization) {
                    amount = (amount / servingSize) * 100.0;
                }
                
                String nutrientName = nutrientInfo.has("name") 
                    ? nutrientInfo.get("name").asText().toLowerCase() 
                    : "";

                // Parse by nutrient ID (most reliable)
                switch (nutrientId) {
                    // Energy & Macronutrients
                    case 1008: // Energy (kcal)
                        calorie = BigDecimal.valueOf(amount);
                        break;
                    case 1003: // Protein
                        protein = BigDecimal.valueOf(amount);
                        break;
                    case 1079: // Fiber, total dietary
                        fiber = BigDecimal.valueOf(amount);
                        break;
                        
                    // Vitamins
                    case 1162: // Vitamin C (Ascorbic acid)
                        vitamins.put("vitaminC", amount);
                        break;
                    case 1106: // Vitamin A, RAE (preferred)
                        vitamins.put("vitaminA", amount);
                        break;
                    case 1104: // Vitamin A, IU (use if RAE not available)
                        if (!vitamins.containsKey("vitaminA")) {
                            vitamins.put("vitaminA", amount);
                        }
                        break;
                    case 1109: // Vitamin E
                        vitamins.put("vitaminE", amount);
                        break;
                    case 1110: // Vitamin K
                        vitamins.put("vitaminK", amount);
                        break;
                    case 1165: // Thiamin (B1)
                        vitamins.put("vitaminB1", amount);
                        break;
                    case 1166: // Riboflavin (B2)
                        vitamins.put("vitaminB2", amount);
                        break;
                    case 1167: // Niacin (B3)
                        vitamins.put("vitaminB3", amount);
                        break;
                    case 1175: // Vitamin B6
                        vitamins.put("vitaminB6", amount);
                        break;
                    case 1177: // Folate, total
                        vitamins.put("folate", amount);
                        break;
                    case 1178: // Vitamin B12
                        vitamins.put("vitaminB12", amount);
                        break;
                        
                    // Minerals
                    case 1087: // Calcium
                        minerals.put("calcium", amount);
                        break;
                    case 1089: // Iron
                        minerals.put("iron", amount);
                        break;
                    case 1092: // Potassium
                        minerals.put("potassium", amount);
                        break;
                    case 1093: // Sodium
                        minerals.put("sodium", amount);
                        break;
                    case 1095: // Zinc
                        minerals.put("zinc", amount);
                        break;
                    case 1098: // Magnesium
                        minerals.put("magnesium", amount);
                        break;
                    case 1099: // Phosphorus
                        minerals.put("phosphorus", amount);
                        break;
                    case 1100: // Copper
                        minerals.put("copper", amount);
                        break;
                    case 1101: // Manganese
                        minerals.put("manganese", amount);
                        break;
                    case 1103: // Selenium
                        minerals.put("selenium", amount);
                        break;
                        
                    default:
                        // Fallback: Parse by name for additional nutrients
                        parseByNutrientName(nutrientName, amount, vitamins, minerals);
                        break;
                }
            }

            // Fallback: If not found by ID, try parsing by name
            if (calorie == null || protein == null || fiber == null) {
                for (JsonNode nutrient : foodNutrients) {
                    if (!nutrient.has("nutrient") || !nutrient.has("amount")) {
                        continue;
                    }
                    JsonNode nutrientInfo = nutrient.get("nutrient");
                    String name = nutrientInfo.has("name") 
                        ? nutrientInfo.get("name").asText().toLowerCase() 
                        : "";
                    double amount = nutrient.get("amount").asDouble(0.0);
                    
                    if (needsNormalization) {
                        amount = (amount / servingSize) * 100.0;
                    }
                    
                    if (calorie == null && (name.contains("energy") || name.contains("calorie"))) {
                        calorie = BigDecimal.valueOf(amount);
                    }
                    if (protein == null && name.contains("protein")) {
                        protein = BigDecimal.valueOf(amount);
                    }
                    if (fiber == null && name.contains("fiber")) {
                        fiber = BigDecimal.valueOf(amount);
                    }
                }
            }

            // Set defaults if still missing
            if (calorie == null) {
                log.warn("No calorie data found for: {}", ingredientName);
                calorie = BigDecimal.ZERO;
            }
            if (protein == null) {
                log.warn("No protein data found for: {}", ingredientName);
                protein = BigDecimal.ZERO;
            }
            if (fiber == null) {
                log.warn("No fiber data found for: {}", ingredientName);
                fiber = BigDecimal.ZERO;
            }

            result.put("calorie", calorie.doubleValue());
            result.put("protein", protein.doubleValue());
            result.put("fiber", fiber.doubleValue());
            result.put("vitamins", vitamins);
            result.put("minerals", minerals);

            log.info("Successfully parsed USDA data for: {} - Calorie: {}, Protein: {}, Fiber: {}, " +
                    "Vitamins: {}, Minerals: {}", 
                    ingredientName, calorie, protein, fiber, vitamins.size(), minerals.size());

        } catch (Exception e) {
            log.error("Error parsing USDA data for {}: {}", ingredientName, e.getMessage(), e);
            throw new RuntimeException("Failed to parse USDA data: " + e.getMessage(), e);
        }

        return result;
    }

    /**
     * Parse nutrients by name as fallback
     */
    private void parseByNutrientName(String name, double amount, 
                                     Map<String, Double> vitamins, 
                                     Map<String, Double> minerals) {
        if (name.contains("vitamin c") || name.contains("ascorbic")) {
            if (!vitamins.containsKey("vitaminC")) {
                vitamins.put("vitaminC", amount);
            }
        } else if (name.contains("vitamin a") && !name.contains("beta")) {
            if (!vitamins.containsKey("vitaminA")) {
                vitamins.put("vitaminA", amount);
            }
        } else if (name.contains("vitamin e") || name.contains("tocopherol")) {
            if (!vitamins.containsKey("vitaminE")) {
                vitamins.put("vitaminE", amount);
            }
        } else if (name.contains("vitamin k") || name.contains("phylloquinone")) {
            if (!vitamins.containsKey("vitaminK")) {
                vitamins.put("vitaminK", amount);
            }
        } else if (name.contains("thiamin") || name.contains("vitamin b1")) {
            if (!vitamins.containsKey("vitaminB1")) {
                vitamins.put("vitaminB1", amount);
            }
        } else if (name.contains("riboflavin") || name.contains("vitamin b2")) {
            if (!vitamins.containsKey("vitaminB2")) {
                vitamins.put("vitaminB2", amount);
            }
        } else if (name.contains("niacin") || name.contains("vitamin b3")) {
            if (!vitamins.containsKey("vitaminB3")) {
                vitamins.put("vitaminB3", amount);
            }
        } else if (name.contains("vitamin b6") || name.contains("pyridoxine")) {
            if (!vitamins.containsKey("vitaminB6")) {
                vitamins.put("vitaminB6", amount);
            }
        } else if (name.contains("folate") || name.contains("folic acid")) {
            if (!vitamins.containsKey("folate")) {
                vitamins.put("folate", amount);
            }
        } else if (name.contains("vitamin b12") || name.contains("cobalamin")) {
            if (!vitamins.containsKey("vitaminB12")) {
                vitamins.put("vitaminB12", amount);
            }
        } else if (name.contains("calcium")) {
            if (!minerals.containsKey("calcium")) {
                minerals.put("calcium", amount);
            }
        } else if (name.contains("iron")) {
            if (!minerals.containsKey("iron")) {
                minerals.put("iron", amount);
            }
        } else if (name.contains("potassium")) {
            if (!minerals.containsKey("potassium")) {
                minerals.put("potassium", amount);
            }
        } else if (name.contains("sodium")) {
            if (!minerals.containsKey("sodium")) {
                minerals.put("sodium", amount);
            }
        } else if (name.contains("zinc")) {
            if (!minerals.containsKey("zinc")) {
                minerals.put("zinc", amount);
            }
        } else if (name.contains("magnesium")) {
            if (!minerals.containsKey("magnesium")) {
                minerals.put("magnesium", amount);
            }
        } else if (name.contains("phosphorus")) {
            if (!minerals.containsKey("phosphorus")) {
                minerals.put("phosphorus", amount);
            }
        } else if (name.contains("copper")) {
            if (!minerals.containsKey("copper")) {
                minerals.put("copper", amount);
            }
        } else if (name.contains("manganese")) {
            if (!minerals.containsKey("manganese")) {
                minerals.put("manganese", amount);
            }
        } else if (name.contains("selenium")) {
            if (!minerals.containsKey("selenium")) {
                minerals.put("selenium", amount);
            }
        }
    }

    /**
     * Extract a simplified summary from USDA data for AI processing
     * This reduces the amount of data sent to OpenAI
     */
    public String extractSummaryForAI(JsonNode usdaData) {
        try {
            StringBuilder summary = new StringBuilder();
            
            if (usdaData.has("description")) {
                summary.append("Food: ").append(usdaData.get("description").asText()).append("\n");
            }
            
            JsonNode foodNutrients = usdaData.get("foodNutrients");
            if (foodNutrients != null && foodNutrients.isArray()) {
                summary.append("Key Nutrients (per 100g):\n");
                int count = 0;
                for (JsonNode nutrient : foodNutrients) {
                    if (count >= 20) break; // Limit to first 20 nutrients
                    if (nutrient.has("nutrient") && nutrient.has("amount")) {
                        JsonNode nutrientInfo = nutrient.get("nutrient");
                        String name = nutrientInfo.has("name") ? nutrientInfo.get("name").asText() : "Unknown";
                        double amount = nutrient.get("amount").asDouble(0.0);
                        summary.append("- ").append(name).append(": ").append(amount);
                        if (nutrient.has("unitName")) {
                            summary.append(" ").append(nutrient.get("unitName").asText());
                        }
                        summary.append("\n");
                        count++;
                    }
                }
            }
            
            return summary.toString();
        } catch (Exception e) {
            log.error("Error extracting summary for AI: {}", e.getMessage(), e);
            return "";
        }
    }
}

