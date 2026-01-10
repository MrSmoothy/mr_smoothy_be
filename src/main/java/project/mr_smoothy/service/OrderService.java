package project.mr_smoothy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.mr_smoothy.dto.request.GuestOrderCreateRequest;
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
    private final PredefinedDrinkRepository predefinedDrinkRepository;
    private final FruitRepository fruitRepository;
    private final CupSizeRepository cupSizeRepository;

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

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders() {
        User currentUser = authUtil.getCurrentUser();
        List<Order> orders = orderRepository.findMyOrders(currentUser.getId());
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        User currentUser = authUtil.getCurrentUser();
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Check if order belongs to user (or is a guest order)
        if (order.getUser() != null && !order.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have permission to view this order");
        }

        return convertToResponse(order);
    }

    /**
     * Creates an order for guest users (without authentication).
     * This method follows OOP principles by encapsulating the guest order creation logic.
     * 
     * @param request The guest order creation request
     * @return OrderResponse containing the created order
     */
    @Transactional
    public OrderResponse createGuestOrder(GuestOrderCreateRequest request) {
        log.info("Creating guest order for customer: {}", request.getCustomerName());
        
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(null); // Guest order has no user
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setPickupTime(request.getPickupTime());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setNotes(request.getNotes());
        order.setStatus(Order.OrderStatus.PENDING);

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (GuestOrderCreateRequest.GuestOrderItemRequest itemRequest : request.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            
            // Set type
            if ("PREDEFINED".equals(itemRequest.getType())) {
                orderItem.setType(OrderItem.ItemType.PREDEFINED);
            } else {
                orderItem.setType(OrderItem.ItemType.CUSTOM);
            }
            
            // Set cup size
            CupSize cupSize = cupSizeRepository.findById(itemRequest.getCupSizeId())
                    .orElseThrow(() -> new RuntimeException("Cup size not found: " + itemRequest.getCupSizeId()));
            orderItem.setCupSize(cupSize);
            
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(itemRequest.getUnitPrice());
            orderItem.setTotalPrice(itemRequest.getTotalPrice());

            // Set predefined drink if applicable
            if (orderItem.getType() == OrderItem.ItemType.PREDEFINED && itemRequest.getPredefinedDrinkId() != null) {
                PredefinedDrink predefinedDrink = predefinedDrinkRepository.findById(itemRequest.getPredefinedDrinkId())
                        .orElseThrow(() -> new RuntimeException("Predefined drink not found: " + itemRequest.getPredefinedDrinkId()));
                orderItem.setPredefinedDrink(predefinedDrink);
            }

            // Set fruits
            if (itemRequest.getFruits() != null) {
                for (GuestOrderCreateRequest.GuestOrderItemFruitRequest fruitRequest : itemRequest.getFruits()) {
                    Fruit fruit = fruitRepository.findById(fruitRequest.getFruitId())
                            .orElseThrow(() -> new RuntimeException("Fruit not found: " + fruitRequest.getFruitId()));
                    
                    OrderItemFruit orderItemFruit = new OrderItemFruit();
                    orderItemFruit.setOrderItem(orderItem);
                    orderItemFruit.setFruit(fruit);
                    orderItemFruit.setQuantity(fruitRequest.getQuantity());
                    orderItem.getFruits().add(orderItemFruit);
                }
            }

            order.getItems().add(orderItem);
            totalPrice = totalPrice.add(orderItem.getTotalPrice());
        }

        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        log.info("Guest order created: {} for customer: {}", savedOrder.getId(), request.getCustomerName());
        return convertToResponse(savedOrder);
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
        List<Order> orders = orderRepository.findAllWithDetails();
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
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToResponse(order);
    }

    /**
     * Gets guest orders by phone number (public API).
     * This method follows OOP principles by encapsulating the guest order retrieval logic.
     * 
     * @param phoneNumber The phone number
     * @return List of guest orders
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getGuestOrdersByPhoneNumber(String phoneNumber) {
        log.info("Fetching guest orders for phone number: {}", phoneNumber);
        List<Order> orders = orderRepository.findByUserIsNullAndPhoneNumberOrderByCreatedAtDescWithDetails(phoneNumber);
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets guest order by ID (public API).
     * This method follows OOP principles by encapsulating the guest order retrieval logic.
     * 
     * @param orderId The order ID
     * @return OrderResponse
     */
    @Transactional(readOnly = true)
    public OrderResponse getGuestOrderById(Long orderId) {
        log.info("Fetching guest order: {}", orderId);
        Order order = orderRepository.findByIdAndUserIsNullWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Guest order not found"));
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
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        try {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(statusString.toUpperCase());
            order.setStatus(newStatus);
            Order savedOrder = orderRepository.save(order);
            log.info("Order {} status updated to {}", orderId, newStatus);
            // Reload with details to ensure all relationships are loaded for response
            return convertToResponse(orderRepository.findByIdWithDetails(savedOrder.getId())
                    .orElse(savedOrder));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + statusString);
        }
    }

    /**
     * Deletes an order (admin only).
     * This method deletes the order and all associated order items.
     * 
     * @param orderId The order ID to delete
     */
    @Transactional
    public void deleteOrder(Long orderId) {
        log.info("Deleting order {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        orderRepository.delete(order);
        log.info("Order {} deleted successfully", orderId);
    }

    private OrderResponse convertToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::convertItemToResponse)
                .collect(Collectors.toList());

        OrderResponse.OrderResponseBuilder builder = OrderResponse.builder()
                .orderId(order.getId())
                .items(itemResponses)
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus().name())
                .pickupTime(order.getPickupTime())
                .phoneNumber(order.getPhoneNumber())
                .notes(order.getNotes())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt());

        // Add user information if order belongs to a logged-in user
        if (order.getUser() != null) {
            builder.username(order.getUser().getUsername())
                   .userEmail(order.getUser().getEmail())
                   .userFullName(order.getUser().getFullName());
        }

        return builder.build();
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

