package project.mr_smoothy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.mr_smoothy.dto.request.PredefinedDrinkCreateRequest;
import project.mr_smoothy.dto.request.PredefinedDrinkUpdateRequest;
import project.mr_smoothy.dto.response.PredefinedDrinkResponse;
import project.mr_smoothy.entity.Fruit;
import project.mr_smoothy.entity.PredefinedDrink;
import project.mr_smoothy.entity.PredefinedDrinkFruit;
import project.mr_smoothy.repository.CartItemRepository;
import project.mr_smoothy.repository.FruitRepository;
import project.mr_smoothy.repository.OrderItemRepository;
import project.mr_smoothy.repository.PredefinedDrinkFruitRepository;
import project.mr_smoothy.repository.PredefinedDrinkRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PredefinedDrinkService {

    private final PredefinedDrinkRepository drinkRepository;
    private final PredefinedDrinkFruitRepository drinkFruitRepository;
    private final FruitRepository fruitRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;

    /**
     * Creates a new predefined drink entity with the provided request data.
     * This method follows OOP principles by encapsulating the creation logic
     * and ensuring all fields are properly set including description and ingredients.
     * 
     * @param request The create request containing field values
     * @return PredefinedDrinkResponse containing the created drink data
     * @throws RuntimeException if drink name already exists or fruit is not found
     */
    public PredefinedDrinkResponse create(PredefinedDrinkCreateRequest request) {
        if (drinkRepository.existsByNameIgnoreCase(request.getName())) {
            throw new RuntimeException("Drink name already exists");
        }
        
        PredefinedDrink drink = createDrinkEntity(request);
        PredefinedDrink savedDrink = drinkRepository.save(drink);
        
        if (request.getIngredients() != null && !request.getIngredients().isEmpty()) {
            List<PredefinedDrinkFruit> ingredients = createDrinkIngredientsFromCreateRequest(
                    savedDrink, 
                    request.getIngredients());
            drinkFruitRepository.saveAll(ingredients);
            savedDrink.setIngredients(ingredients);
        }
        
        return toResponse(savedDrink);
    }
    
    /**
     * Creates a new drink entity from the create request.
     * This method encapsulates the entity creation logic following OOP principles.
     * 
     * @param request The create request containing field values
     * @return PredefinedDrink entity with fields set from request
     */
    private PredefinedDrink createDrinkEntity(PredefinedDrinkCreateRequest request) {
        PredefinedDrink drink = new PredefinedDrink();
        drink.setName(request.getName());
        drink.setDescription(request.getDescription());
        drink.setImageUrl(request.getImageUrl());
        drink.setBasePrice(request.getBasePrice()); // ราคาพื้นฐาน (ถ้า null จะคำนวณจากส่วนผสม)
        drink.setCategory(request.getCategory() != null 
                ? request.getCategory() 
                : PredefinedDrink.Category.SIGNATURE);
        drink.setActive(request.getActive() != null ? request.getActive() : true);
        return drink;
    }
    
    /**
     * Creates drink ingredient entities from the create request ingredient data.
     * This method encapsulates the ingredient creation logic following OOP principles.
     * 
     * @param drink The drink entity to associate ingredients with
     * @param createIngredients The list of create request ingredients
     * @return List of created PredefinedDrinkFruit entities
     * @throws RuntimeException if any fruit is not found
     */
    private List<PredefinedDrinkFruit> createDrinkIngredientsFromCreateRequest(
            PredefinedDrink drink, 
            List<PredefinedDrinkCreateRequest.Ingredient> createIngredients) {
        List<PredefinedDrinkFruit> ingredients = new ArrayList<>();
        
        for (PredefinedDrinkCreateRequest.Ingredient createIngredient : createIngredients) {
            Fruit fruit = fruitRepository.findById(createIngredient.getFruitId())
                    .orElseThrow(() -> new RuntimeException("Fruit not found: " + createIngredient.getFruitId()));
            
            PredefinedDrinkFruit drinkFruit = new PredefinedDrinkFruit();
            drinkFruit.setDrink(drink);
            drinkFruit.setFruit(fruit);
            drinkFruit.setQuantity(createIngredient.getQuantity());
            
            ingredients.add(drinkFruit);
        }
        
        return ingredients;
    }

    /**
     * Updates an existing predefined drink entity with the provided request data.
     * This method follows OOP principles by encapsulating the update logic
     * and ensuring all fields are properly updated including description and ingredients.
     * 
     * @param id The ID of the drink to update
     * @param request The update request containing new field values
     * @return PredefinedDrinkResponse containing the updated drink data
     * @throws RuntimeException if drink is not found
     */
    public PredefinedDrinkResponse update(Long id, PredefinedDrinkUpdateRequest request) {
        PredefinedDrink drink = drinkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Drink not found"));
        
        updateDrinkFields(drink, request);
        
        // Update ingredients before saving to avoid orphan removal issues
        if (request.getIngredients() != null) {
            updateDrinkIngredients(drink, request);
        }
        
        // Save and flush the drink entity to ensure all changes are persisted immediately
        // This ensures orphan removal is processed before reloading
        PredefinedDrink savedDrink = drinkRepository.saveAndFlush(drink);
        
        // Reload the entity to ensure the ingredients collection is properly loaded
        // This is necessary because JPA may cache the collection state
        PredefinedDrink refreshedDrink = drinkRepository.findById(savedDrink.getId())
                .orElseThrow(() -> new RuntimeException("Drink not found after update"));
        
        return toResponse(refreshedDrink);
    }
    
    /**
     * Updates the drink entity fields based on the update request.
     * This method encapsulates the field update logic following OOP principles.
     * Description is always updated when provided in the request, including empty strings.
     * 
     * @param drink The drink entity to update
     * @param request The update request containing new field values
     */
    private void updateDrinkFields(PredefinedDrink drink, PredefinedDrinkUpdateRequest request) {
        if (request.getName() != null) {
            drink.setName(request.getName());
        }
        
        // Description is always updated when provided in the request
        // Frontend always sends description field when updating
        // Empty string is a valid value and should be saved
        if (request.getDescription() != null) {
            drink.setDescription(request.getDescription());
        }
        
        if (request.getImageUrl() != null) {
            drink.setImageUrl(request.getImageUrl());
        }
        
        // basePrice can be null (will calculate from ingredients) or a specific value
        // ถ้า request มี basePrice (รวมทั้ง null) ให้อัพเดต
        if (request.getBasePrice() != null) {
            drink.setBasePrice(request.getBasePrice());
        } else if (request.getName() != null || request.getDescription() != null || request.getImageUrl() != null) {
            // ถ้ามีการอัพเดต field อื่นๆ แต่ไม่ได้ส่ง basePrice มา ให้ตั้งเป็น null (จะคำนวณจากส่วนผสม)
            // แต่ถ้าไม่ได้ส่ง basePrice มาเลย (undefined) ก็ไม่ต้องอัพเดต
            // ใช้วิธีตรวจสอบว่า request มี basePrice field หรือไม่โดยดูจาก request object
        }
        
        if (request.getCategory() != null) {
            drink.setCategory(request.getCategory());
        }
        
        if (request.getActive() != null) {
            drink.setActive(request.getActive());
        }
    }
    
    /**
     * Updates the drink ingredients based on the update request.
     * This method encapsulates the ingredients update logic following OOP principles.
     * When updating ingredients, we clear the existing collection and add new ones.
     * This approach works correctly with orphanRemoval = true in JPA.
     * 
     * @param drink The drink entity to update ingredients for
     * @param request The update request containing new ingredient values
     */
    private void updateDrinkIngredients(PredefinedDrink drink, PredefinedDrinkUpdateRequest request) {
        // Clear existing ingredients first
        // With orphanRemoval = true, clearing the collection will automatically delete orphaned entities
        drink.getIngredients().clear();
        
        // Create and add new ingredients to the collection
        List<PredefinedDrinkFruit> newIngredients = createDrinkIngredients(drink, request.getIngredients());
        
        // Add new ingredients to the collection (orphan removal will handle deletion of old ones)
        for (PredefinedDrinkFruit ingredient : newIngredients) {
            drink.getIngredients().add(ingredient);
        }
    }
    
    /**
     * Creates drink ingredient entities from the ingredient request data.
     * This method encapsulates the ingredient creation logic following OOP principles.
     * 
     * @param drink The drink entity to associate ingredients with
     * @param ingredientRequests The list of ingredient requests
     * @return List of created PredefinedDrinkFruit entities
     * @throws RuntimeException if any fruit is not found
     */
    private List<PredefinedDrinkFruit> createDrinkIngredients(
            PredefinedDrink drink, 
            List<PredefinedDrinkUpdateRequest.Ingredient> ingredientRequests) {
        List<PredefinedDrinkFruit> ingredients = new ArrayList<>();
        
        for (PredefinedDrinkUpdateRequest.Ingredient ingredientRequest : ingredientRequests) {
            Fruit fruit = fruitRepository.findById(ingredientRequest.getFruitId())
                    .orElseThrow(() -> new RuntimeException("Fruit not found: " + ingredientRequest.getFruitId()));
            
            PredefinedDrinkFruit drinkFruit = new PredefinedDrinkFruit();
            drinkFruit.setDrink(drink);
            drinkFruit.setFruit(fruit);
            drinkFruit.setQuantity(ingredientRequest.getQuantity());
            
            ingredients.add(drinkFruit);
        }
        
        return ingredients;
    }

    public void delete(Long id) {
        PredefinedDrink drink = drinkRepository.findById(id).orElseThrow(() -> new RuntimeException("Drink not found"));
        
        // ตรวจสอบว่ามี OrderItem ที่อ้างอิงถึง drink นี้หรือไม่
        boolean hasOrderItems = orderItemRepository.existsByPredefinedDrinkId(id);
        if (hasOrderItems) {
            throw new RuntimeException("ไม่สามารถลบเมนูนี้ได้ เนื่องจากมีออเดอร์ที่ใช้เมนูนี้อยู่ กรุณาปิดการใช้งาน (Active = false) แทน");
        }
        
        // ตรวจสอบว่ามี CartItem ที่อ้างอิงถึง drink นี้หรือไม่
        boolean hasCartItems = cartItemRepository.existsByPredefinedDrinkId(id);
        if (hasCartItems) {
            throw new RuntimeException("ไม่สามารถลบเมนูนี้ได้ เนื่องจากมีสินค้าในตะกร้าที่ใช้เมนูนี้อยู่ กรุณาปิดการใช้งาน (Active = false) แทน");
        }
        
        drinkRepository.delete(drink);
    }

    @Transactional(readOnly = true)
    public PredefinedDrinkResponse get(Long id) {
        return drinkRepository.findByIdWithIngredients(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Drink not found"));
    }

    @Transactional(readOnly = true)
    public List<PredefinedDrinkResponse> list() {
        return drinkRepository.findAllWithIngredients().stream()
                .filter(d -> d.getActive())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PredefinedDrinkResponse> listAll() {
        // สำหรับ admin ให้แสดงทุกเมนู รวมทั้งที่ปิดการใช้งาน
        return drinkRepository.findAllWithIngredients().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private PredefinedDrinkResponse toResponse(PredefinedDrink d) {
        return PredefinedDrinkResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .imageUrl(d.getImageUrl())
                .basePrice(d.getBasePrice())
                .category(d.getCategory())
                .active(d.getActive())
                .ingredients(d.getIngredients().stream().map(df -> PredefinedDrinkResponse.IngredientInfo.builder()
                        .fruitId(df.getFruit().getId())
                        .fruitName(df.getFruit().getName())
                        .quantity(df.getQuantity())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}


