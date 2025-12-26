package project.mr_smoothy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.mr_smoothy.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    @Query("SELECT COUNT(ci) > 0 FROM CartItem ci WHERE ci.predefinedDrink.id = :drinkId")
    boolean existsByPredefinedDrinkId(@Param("drinkId") Long drinkId);
}


