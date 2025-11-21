package project.mr_smoothy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.mr_smoothy.entity.Fruit;

import java.util.Optional;

public interface FruitRepository extends JpaRepository<Fruit, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Fruit> findByNameIgnoreCase(String name);
    java.util.List<Fruit> findByActiveTrue();
    java.util.List<Fruit> findByActiveTrueAndSeasonalTrue();
}


