package project.mr_smoothy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.mr_smoothy.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.predefinedDrink.id = :drinkId")
    boolean existsByPredefinedDrinkId(@Param("drinkId") Long drinkId);
}

