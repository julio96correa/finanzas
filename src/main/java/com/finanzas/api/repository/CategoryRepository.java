package com.finanzas.api.repository;

import com.finanzas.api.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByUserUserId(UUID userId);
    List<Category> findByUserUserIdAndType(UUID userId, String type);
}
