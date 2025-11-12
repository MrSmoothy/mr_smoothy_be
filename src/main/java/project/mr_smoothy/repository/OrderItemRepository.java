package project.mr_smoothy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.mr_smoothy.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}

