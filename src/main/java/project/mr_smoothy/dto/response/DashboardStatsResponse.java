package project.mr_smoothy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {
    private Long totalOrders;
    private BigDecimal revenue;
    private String bestSellingDrink;
    private Long totalUsers;
    private Long activeUsers;
    private BigDecimal totalSales;
    private BigDecimal totalCost;
    private BigDecimal profit;
    private String revenueChangePercent; // e.g., "+10%"
    private String ordersChangePercent;
    private String bestSellChangePercent;
}

