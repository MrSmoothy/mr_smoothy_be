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
import project.mr_smoothy.repository.FruitRepository;
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

    public PredefinedDrinkResponse create(PredefinedDrinkCreateRequest request) {
        if (drinkRepository.existsByNameIgnoreCase(request.getName())) {
            throw new RuntimeException("Drink name already exists");
        }
        PredefinedDrink drink = new PredefinedDrink();
        drink.setName(request.getName());
        drink.setDescription(request.getDescription());
        drink.setImageUrl(request.getImageUrl());
        drink.setActive(request.getActive() != null ? request.getActive() : true);
        PredefinedDrink saved = drinkRepository.save(drink);

        if (request.getIngredients() != null) {
            List<PredefinedDrinkFruit> ing = new ArrayList<>();
            for (PredefinedDrinkCreateRequest.Ingredient i : request.getIngredients()) {
                Fruit fruit = fruitRepository.findById(i.getFruitId()).orElseThrow(() -> new RuntimeException("Fruit not found: " + i.getFruitId()));
                PredefinedDrinkFruit dfi = new PredefinedDrinkFruit();
                dfi.setDrink(saved);
                dfi.setFruit(fruit);
                dfi.setQuantity(i.getQuantity());
                ing.add(dfi);
            }
            drinkFruitRepository.saveAll(ing);
            saved.setIngredients(ing);
        }
        return toResponse(saved);
    }

    public PredefinedDrinkResponse update(Long id, PredefinedDrinkUpdateRequest request) {
        PredefinedDrink drink = drinkRepository.findById(id).orElseThrow(() -> new RuntimeException("Drink not found"));
        if (request.getName() != null) drink.setName(request.getName());
        if (request.getDescription() != null) drink.setDescription(request.getDescription());
        if (request.getImageUrl() != null) drink.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) drink.setActive(request.getActive());
        PredefinedDrink saved = drinkRepository.save(drink);

        if (request.getIngredients() != null) {
            drinkFruitRepository.deleteAll(saved.getIngredients());
            List<PredefinedDrinkFruit> ing = new ArrayList<>();
            for (PredefinedDrinkUpdateRequest.Ingredient i : request.getIngredients()) {
                Fruit fruit = fruitRepository.findById(i.getFruitId()).orElseThrow(() -> new RuntimeException("Fruit not found: " + i.getFruitId()));
                PredefinedDrinkFruit dfi = new PredefinedDrinkFruit();
                dfi.setDrink(saved);
                dfi.setFruit(fruit);
                dfi.setQuantity(i.getQuantity());
                ing.add(dfi);
            }
            drinkFruitRepository.saveAll(ing);
            saved.setIngredients(ing);
        }
        return toResponse(saved);
    }

    public void delete(Long id) {
        PredefinedDrink drink = drinkRepository.findById(id).orElseThrow(() -> new RuntimeException("Drink not found"));
        drinkRepository.delete(drink);
    }

    public PredefinedDrinkResponse get(Long id) {
        return drinkRepository.findById(id).map(this::toResponse).orElseThrow(() -> new RuntimeException("Drink not found"));
    }

    public List<PredefinedDrinkResponse> list() {
        return drinkRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private PredefinedDrinkResponse toResponse(PredefinedDrink d) {
        return PredefinedDrinkResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .imageUrl(d.getImageUrl())
                .active(d.getActive())
                .ingredients(d.getIngredients().stream().map(df -> PredefinedDrinkResponse.IngredientInfo.builder()
                        .fruitId(df.getFruit().getId())
                        .fruitName(df.getFruit().getName())
                        .quantity(df.getQuantity())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}


