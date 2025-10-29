package project.mr_smoothy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.mr_smoothy.entity.PredefinedDrink;

public interface PredefinedDrinkRepository extends JpaRepository<PredefinedDrink, Long> {
    boolean existsByNameIgnoreCase(String name);
}


