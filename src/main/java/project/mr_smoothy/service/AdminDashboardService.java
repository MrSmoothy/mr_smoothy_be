package project.mr_smoothy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.mr_smoothy.dto.response.DashboardStatsResponse;
import project.mr_smoothy.entity.Order;
import project.mr_smoothy.entity.User;
import project.mr_smoothy.repository.OrderRepository;
import project.mr_smoothy.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminDashboardService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    /**
     * Gets dashboard statistics for admin.
     * This method follows OOP principles by encapsulating the statistics calculation logic.
     * 
     * @return DashboardStatsResponse containing all dashboard metrics
     */
    public DashboardStatsResponse getDashboardStats() {
        log.info("Fetching dashboard statistics for admin");

        // Get all orders
        List<Order> allOrders = orderRepository.findAll();
        if (allOrders == null) {
            allOrders = new java.util.ArrayList<>();
        }
        
        // Calculate total orders
        Long totalOrders = (long) allOrders.size();
        
        // Calculate revenue (sum of all order total prices)
        BigDecimal revenue = allOrders.stream()
                .filter(order -> order.getTotalPrice() != null)
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Find best selling drink
        String bestSellingDrink = findBestSellingDrink(allOrders);
        
        // Get all users
        List<User> allUsers = userRepository.findAll();
        if (allUsers == null) {
            allUsers = new java.util.ArrayList<>();
        }
        Long totalUsers = (long) allUsers.size();
        
        // Calculate active users (users who have placed at least one order)
        Long activeUsers = allUsers.stream()
                .filter(user -> {
                    try {
                        List<Order> userOrders = orderRepository.findByUser(user);
                        return userOrders != null && !userOrders.isEmpty();
                    } catch (Exception e) {
                        log.warn("Error checking orders for user {}: {}", user.getId(), e.getMessage());
                        return false;
                    }
                })
                .count();
        
        // Total sales (same as revenue for now)
        BigDecimal totalSales = revenue;
        
        // Calculate total cost (sum of ingredient costs)
        BigDecimal totalCost = calculateTotalCost(allOrders);
        
        // Calculate profit
        BigDecimal profit = revenue.subtract(totalCost);
        
        // Calculate percentage changes (comparing last month to this month)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonth = now.minus(1, ChronoUnit.MONTHS);
        LocalDateTime twoMonthsAgo = lastMonth.minus(1, ChronoUnit.MONTHS);
        
        long ordersThisMonth = allOrders.stream()
                .filter(order -> order.getCreatedAt() != null && 
                        order.getCreatedAt().isAfter(lastMonth))
                .count();
        long ordersLastMonth = allOrders.stream()
                .filter(order -> order.getCreatedAt() != null && 
                        order.getCreatedAt().isAfter(twoMonthsAgo) &&
                        order.getCreatedAt().isBefore(lastMonth))
                .count();
        
        String ordersChangePercent = calculatePercentChange(ordersThisMonth, ordersLastMonth);
        
        BigDecimal revenueThisMonth = allOrders.stream()
                .filter(order -> order.getCreatedAt() != null && 
                        order.getCreatedAt().isAfter(lastMonth))
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal revenueLastMonth = allOrders.stream()
                .filter(order -> order.getCreatedAt() != null && 
                        order.getCreatedAt().isAfter(twoMonthsAgo) &&
                        order.getCreatedAt().isBefore(lastMonth))
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        String revenueChangePercent = calculatePercentChange(revenueThisMonth, revenueLastMonth);
        
        // Best sell change (simplified - using same logic as orders)
        String bestSellChangePercent = ordersChangePercent;
        
        return DashboardStatsResponse.builder()
                .totalOrders(totalOrders)
                .revenue(revenue)
                .bestSellingDrink(bestSellingDrink)
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalSales(totalSales)
                .totalCost(totalCost)
                .profit(profit)
                .revenueChangePercent(revenueChangePercent)
                .ordersChangePercent(ordersChangePercent)
                .bestSellChangePercent(bestSellChangePercent)
                .build();
    }

    /**
     * Finds the best selling drink by counting order items.
     * This method encapsulates the best-selling calculation logic.
     */
    private String findBestSellingDrink(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return "N/A";
        }
        
        try {
            Map<String, Long> drinkCounts = orders.stream()
                    .filter(order -> order.getItems() != null)
                    .flatMap(order -> order.getItems().stream())
                    .filter(item -> item != null && item.getPredefinedDrink() != null && item.getPredefinedDrink().getName() != null)
                    .collect(Collectors.groupingBy(
                            item -> item.getPredefinedDrink().getName(),
                            Collectors.counting()
                    ));
            
            if (drinkCounts.isEmpty()) {
                return "N/A";
            }
            
            return drinkCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");
        } catch (Exception e) {
            log.warn("Error finding best selling drink: {}", e.getMessage());
            return "N/A";
        }
    }

    /**
     * Calculates total cost from all orders.
     * This method encapsulates the cost calculation logic.
     */
    private BigDecimal calculateTotalCost(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Simplified calculation - sum of ingredient costs
        // In a real system, you'd calculate actual ingredient costs
        // For now, we'll estimate cost as 60% of revenue
        BigDecimal totalRevenue = orders.stream()
                .filter(order -> order.getTotalPrice() != null)
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return totalRevenue.multiply(new BigDecimal("0.6"));
    }

    /**
     * Calculates percentage change between two values.
     * This method encapsulates the percentage calculation logic.
     */
    private String calculatePercentChange(Number current, Number previous) {
        if (previous.doubleValue() == 0) {
            return current.doubleValue() > 0 ? "+100%" : "0%";
        }
        
        double change = ((current.doubleValue() - previous.doubleValue()) / previous.doubleValue()) * 100;
        String sign = change >= 0 ? "+" : "";
        return String.format("%s%.0f%%", sign, change);
    }
}

