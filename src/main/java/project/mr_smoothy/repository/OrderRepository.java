package project.mr_smoothy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.mr_smoothy.entity.Order;
import project.mr_smoothy.entity.User;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH i.cupSize " +
           "LEFT JOIN FETCH i.predefinedDrink " +
           "WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findMyOrders(@Param("userId") Long userId);
    
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.user " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH i.cupSize " +
           "LEFT JOIN FETCH i.predefinedDrink " +
           "ORDER BY o.createdAt DESC")
    List<Order> findAllWithDetails();
    
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.user " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH i.cupSize " +
           "LEFT JOIN FETCH i.predefinedDrink " +
           "WHERE o.id = :orderId")
    java.util.Optional<Order> findByIdWithDetails(@Param("orderId") Long orderId);
    
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH i.cupSize " +
           "LEFT JOIN FETCH i.predefinedDrink " +
           "WHERE o.user IS NULL AND o.phoneNumber = :phoneNumber ORDER BY o.createdAt DESC")
    List<Order> findByUserIsNullAndPhoneNumberOrderByCreatedAtDescWithDetails(@Param("phoneNumber") String phoneNumber);
    
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH i.cupSize " +
           "LEFT JOIN FETCH i.predefinedDrink " +
           "WHERE o.user IS NULL AND o.id = :orderId")
    java.util.Optional<Order> findByIdAndUserIsNullWithDetails(@Param("orderId") Long orderId);
    
    List<Order> findByUser(User user);
    
    // สำหรับ guest orders
    List<Order> findByUserIsNullAndPhoneNumberOrderByCreatedAtDesc(String phoneNumber);
    
    java.util.Optional<Order> findByIdAndUserIsNull(Long orderId);
}

