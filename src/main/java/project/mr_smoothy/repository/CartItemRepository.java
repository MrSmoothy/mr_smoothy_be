package project.mr_smoothy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.mr_smoothy.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}


