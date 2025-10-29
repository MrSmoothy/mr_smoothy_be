package project.mr_smoothy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.mr_smoothy.entity.CupSize;

public interface CupSizeRepository extends JpaRepository<CupSize, Long> {
    boolean existsByNameIgnoreCase(String name);
    java.util.List<CupSize> findByActiveTrue();
}


