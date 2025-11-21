package project.mr_smoothy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.mr_smoothy.entity.Cart;
import project.mr_smoothy.entity.User;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserAndActiveTrue(User user);
    
    @Query("SELECT DISTINCT c FROM Cart c " +
           "LEFT JOIN FETCH c.items i " +
           "LEFT JOIN FETCH i.cupSize " +
           "LEFT JOIN FETCH i.predefinedDrink " +
           "WHERE c.user = :user AND c.active = true")
    Optional<Cart> findByUserAndActiveTrueWithItems(@Param("user") User user);
}


