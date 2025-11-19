package project.mr_smoothy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.mr_smoothy.dto.request.OrderCreateRequest;
import project.mr_smoothy.dto.response.CartResponse;
import project.mr_smoothy.dto.response.OrderItemResponse;
import project.mr_smoothy.dto.response.OrderResponse;
import project.mr_smoothy.entity.*;
import project.mr_smoothy.repository.*;
import project.mr_smoothy.util.AuthUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final AuthUtil authUtil;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        User currentUser = authUtil.getCurrentUser();
        
        CartResponse cartResponse = cartService.getMyCart();
        
        if (cartResponse == null || cartResponse.getItems() == null || cartResponse.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        Cart cart = cartRepository.findByUserAndActiveTrue(currentUser)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Order order = new Order();
        order.setUser(currentUser);
        order.setPickupTime(request.getPickupTime());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setNotes(request.getNotes());
        order.setStatus(Order.OrderStatus.PENDING);

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setType(cartItem.getType() == CartItem.ItemType.PREDEFINED 
                ? OrderItem.ItemType.PREDEFINED 
                : OrderItem.ItemType.CUSTOM);
            orderItem.setCupSize(cartItem.getCupSize());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setTotalPrice(cartItem.getTotalPrice());

            if (cartItem.getType() == CartItem.ItemType.PREDEFINED) {
                orderItem.setPredefinedDrink(cartItem.getPredefinedDrink());
            }

            for (CartItemFruit cartItemFruit : cartItem.getFruits()) {
                OrderItemFruit orderItemFruit = new OrderItemFruit();
                orderItemFruit.setOrderItem(orderItem);
                orderItemFruit.setFruit(cartItemFruit.getFruit());
                orderItemFruit.setQuantity(cartItemFruit.getQuantity());
                orderItem.getFruits().add(orderItemFruit);
            }

            order.getItems().add(orderItem);
            totalPrice = totalPrice.add(orderItem.getTotalPrice());
        }

        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        cartService.clearMyCart();

        log.info("Order created: {}", savedOrder.getId());
        return convertToResponse(savedOrder);
    }

    public List<OrderResponse> getMyOrders() {
        User currentUser = authUtil.getCurrentUser();
        List<Order> orders = orderRepository.findMyOrders(currentUser.getId());
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long orderId) {
        User currentUser = authUtil.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have permission to view this order");
        }

        return convertToResponse(order);
    }

    /**
     * Gets all orders (admin only).
     * This method follows OOP principles by encapsulating the order retrieval logic.
     * 
     * @return List of all orders
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders (admin)");
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets order by ID without user check (admin only).
     * This method follows OOP principles by encapsulating the order retrieval logic.
     * 
     * @param orderId The order ID
     * @return OrderResponse
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderByIdForAdmin(Long orderId) {
        log.info("Fetching order {} for admin", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToResponse(order);
    }

    /**
     * Updates order status (admin only).
     * This method follows OOP principles by encapsulating the status update logic.
     * 
     * @param orderId The order ID
     * @param statusString The new status
     * @return Updated OrderResponse
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String statusString) {
        log.info("Updating order {} status to {}", orderId, statusString);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        try {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(statusString.toUpperCase());
            order.setStatus(newStatus);
            Order savedOrder = orderRepository.save(order);
            log.info("Order {} status updated to {}", orderId, newStatus);
            return convertToResponse(savedOrder);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + statusString);
        }
    }

    private OrderResponse convertToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::convertItemToResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(order.getId())
                .items(itemResponses)
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus().name())
                .pickupTime(order.getPickupTime())
                .phoneNumber(order.getPhoneNumber())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemResponse convertItemToResponse(OrderItem item) {
        List<OrderItemResponse.FruitEntry> fruitEntries = item.getFruits().stream()
                .map(f -> OrderItemResponse.FruitEntry.builder()
                        .fruitId(f.getFruit().getId())
                        .fruitName(f.getFruit().getName())
                        .quantity(f.getQuantity())
                        .pricePerUnit(f.getFruit().getPricePerUnit())
                        .build())
                .collect(Collectors.toList());

        return OrderItemResponse.builder()
                .id(item.getId())
                .type(item.getType().name())
                .cupSizeName(item.getCupSize().getName())
                .quantity(item.getQuantity())
                .predefinedDrinkName(item.getPredefinedDrink() != null 
                    ? item.getPredefinedDrink().getName() 
                    : null)
                .fruits(fruitEntries)
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}

