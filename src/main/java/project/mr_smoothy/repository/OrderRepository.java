package project.mr_smoothy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.mr_smoothy.entity.Order;
import project.mr_smoothy.entity.User;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findMyOrders(@Param("userId") Long userId);
    
    List<Order> findByUser(User user);
    
    // สำหรับ guest orders
    List<Order> findByUserIsNullAndPhoneNumberOrderByCreatedAtDesc(String phoneNumber);
    
    java.util.Optional<Order> findByIdAndUserIsNull(Long orderId);
}

