package project.mr_smoothy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import project.mr_smoothy.dto.request.CartAddItemRequest;
import project.mr_smoothy.dto.response.CartItemResponse;
import project.mr_smoothy.dto.response.CartResponse;
import project.mr_smoothy.entity.*;
import project.mr_smoothy.repository.*;
import project.mr_smoothy.util.AuthUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CupSizeRepository cupSizeRepository;
    private final FruitRepository fruitRepository;
    private final PredefinedDrinkRepository predefinedDrinkRepository;
    private final AuthUtil authUtil;

    public CartResponse getMyCart() {
        Cart cart = getOrCreateActiveCart(getCurrentUser());
        return toResponse(cart);
    }

    public CartResponse clearMyCart() {
        Cart cart = getOrCreateActiveCart(getCurrentUser());
        cart.getItems().clear();
        return toResponse(cart);
    }

    public CartResponse removeItem(Long itemId) {
        Cart cart = getOrCreateActiveCart(getCurrentUser());
        cart.getItems().removeIf(ci -> ci.getId().equals(itemId));
        return toResponse(cart);
    }

    public CartResponse addItem(CartAddItemRequest request) {
        User user = getCurrentUser();
        Cart cart = getOrCreateActiveCart(user);

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setCupSize(cupSizeRepository.findById(request.getCupSizeId()).orElseThrow(() -> new RuntimeException("Cup size not found")));
        item.setQuantity(request.getQuantity());

        if (request.getType() == CartAddItemRequest.Type.PREDEFINED) {
            PredefinedDrink drink = predefinedDrinkRepository.findById(request.getPredefinedDrinkId())
                    .orElseThrow(() -> new RuntimeException("Predefined drink not found"));
            item.setType(CartItem.ItemType.PREDEFINED);
            item.setPredefinedDrink(drink);
        } else {
            item.setType(CartItem.ItemType.CUSTOM);
            if (request.getIngredients() == null || request.getIngredients().isEmpty()) {
                throw new RuntimeException("Custom drink requires ingredients");
            }
            for (CartAddItemRequest.Ingredient ing : request.getIngredients()) {
                Fruit fruit = fruitRepository.findById(ing.getFruitId()).orElseThrow(() -> new RuntimeException("Fruit not found: " + ing.getFruitId()));
                CartItemFruit cif = new CartItemFruit();
                cif.setCartItem(item);
                cif.setFruit(fruit);
                cif.setQuantity(ing.getQuantity());
                item.getFruits().add(cif);
            }
        }

        cart.getItems().add(item);
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private Cart getOrCreateActiveCart(User user) {
        return cartRepository.findByUserAndActiveTrueWithItems(user)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    c.setActive(true);
                    return cartRepository.save(c);
                });
    }

    private User getCurrentUser() {
        return authUtil.getCurrentUser();
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream().map(ci -> CartItemResponse.builder()
                .id(ci.getId())
                .type(ci.getType().name())
                .quantity(ci.getQuantity())
                .cupSizeId(ci.getCupSize().getId())
                .cupSizeName(ci.getCupSize().getName())
                .predefinedDrinkId(ci.getPredefinedDrink() != null ? ci.getPredefinedDrink().getId() : null)
                .predefinedDrinkName(ci.getPredefinedDrink() != null ? ci.getPredefinedDrink().getName() : null)
                .predefinedDrinkImageUrl(ci.getPredefinedDrink() != null ? ci.getPredefinedDrink().getImageUrl() : null)
                .fruits(ci.getFruits().stream().map(f -> CartItemResponse.FruitEntry.builder()
                        .fruitId(f.getFruit().getId())
                        .fruitName(f.getFruit().getName())
                        .quantity(f.getQuantity())
                        .pricePerUnit(f.getFruit().getPricePerUnit())
                        .build()).collect(Collectors.toList()))
                .unitPrice(ci.getUnitPrice())
                .totalPrice(ci.getTotalPrice())
                .build()).collect(Collectors.toList());

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(items)
                .totalPrice(cart.getTotalPrice())
                .build();
    }
}


