package com.finanzas.api.service;

import com.finanzas.api.dto.request.CategoryRequest;
import com.finanzas.api.dto.response.CategoryResponse;
import com.finanzas.api.model.Category;
import com.finanzas.api.model.User;
import com.finanzas.api.repository.CategoryRepository;
import com.finanzas.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public List<CategoryResponse> getMyCategories() {
        UUID userId = getCurrentUser().getUserId();
        return categoryRepository.findByUserUserId(userId)
            .stream().map(this::toResponse).toList();
    }

    public List<CategoryResponse> getMyCategoriesByType(String type) {
        UUID userId = getCurrentUser().getUserId();
        return categoryRepository.findByUserUserIdAndType(userId, type)
            .stream().map(this::toResponse).toList();
    }

    public CategoryResponse create(CategoryRequest request) {
        User user = getCurrentUser();
        Category category = new Category();
        category.setUser(user);
        category.setTitle(request.getTitle());
        category.setType(request.getType());
        return toResponse(categoryRepository.save(category));
    }

    public CategoryResponse update(UUID id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        validateOwnership(category.getUser().getUserId());
        category.setTitle(request.getTitle());
        category.setType(request.getType());
        return toResponse(categoryRepository.save(category));
    }

    public void delete(UUID id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        validateOwnership(category.getUser().getUserId());
        categoryRepository.delete(category);
    }

    private CategoryResponse toResponse(Category c) {
        CategoryResponse r = new CategoryResponse();
        r.setCategoryId(c.getCategoryId());
        r.setTitle(c.getTitle());
        r.setType(c.getType());
        return r;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private void validateOwnership(UUID ownerId) {
        if (!getCurrentUser().getUserId().equals(ownerId)) {
            throw new RuntimeException("No tienes permiso para esta acción");
        }
    }
}
