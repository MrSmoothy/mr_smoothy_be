package project.mr_smoothy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.mr_smoothy.entity.PredefinedDrink;

import java.util.List;
import java.util.Optional;

public interface PredefinedDrinkRepository extends JpaRepository<PredefinedDrink, Long> {
    boolean existsByNameIgnoreCase(String name);
    
    @Query("SELECT DISTINCT d FROM PredefinedDrink d " +
           "LEFT JOIN FETCH d.ingredients i " +
           "LEFT JOIN FETCH i.fruit " +
           "WHERE d.active = true")
    List<PredefinedDrink> findAllActiveWithIngredients();
    
    @Query("SELECT DISTINCT d FROM PredefinedDrink d " +
           "LEFT JOIN FETCH d.ingredients i " +
           "LEFT JOIN FETCH i.fruit")
    List<PredefinedDrink> findAllWithIngredients();
    
    @Query("SELECT DISTINCT d FROM PredefinedDrink d " +
           "LEFT JOIN FETCH d.ingredients i " +
           "LEFT JOIN FETCH i.fruit " +
           "WHERE d.id = :id")
    Optional<PredefinedDrink> findByIdWithIngredients(@Param("id") Long id);
    
    @Query("SELECT DISTINCT d FROM PredefinedDrink d " +
           "LEFT JOIN FETCH d.ingredients i " +
           "LEFT JOIN FETCH i.fruit " +
           "WHERE d.active = true AND d.popular = true")
    List<PredefinedDrink> findPopularWithIngredients();
}


