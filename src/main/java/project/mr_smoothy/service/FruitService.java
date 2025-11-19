package project.mr_smoothy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.mr_smoothy.dto.request.FruitCreateRequest;
import project.mr_smoothy.dto.request.FruitUpdateRequest;
import project.mr_smoothy.dto.response.FruitResponse;
import project.mr_smoothy.entity.Fruit;
import project.mr_smoothy.repository.FruitRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FruitService {

    private final FruitRepository fruitRepository;

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
        Fruit saved = fruitRepository.save(fruit);
        return toResponse(saved);
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
    }

    public void delete(Long id) {
        if (!fruitRepository.existsById(id)) throw new RuntimeException("Fruit not found");
        fruitRepository.deleteById(id);
    }

    public FruitResponse get(Long id) {
        return fruitRepository.findById(id).map(this::toResponse).orElseThrow(() -> new RuntimeException("Fruit not found"));
    }

    public List<FruitResponse> list() {
        return fruitRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<FruitResponse> listActive() {
        return fruitRepository.findByActiveTrue().stream().map(this::toResponse).collect(Collectors.toList());
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
                .build();
    }
}


