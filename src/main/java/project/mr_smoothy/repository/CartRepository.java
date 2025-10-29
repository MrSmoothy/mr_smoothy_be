package project.mr_smoothy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.mr_smoothy.entity.Cart;
import project.mr_smoothy.entity.User;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserAndActiveTrue(User user);
}


