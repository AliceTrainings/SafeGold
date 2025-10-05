package com.goldloan.demo.service;

import com.goldloan.demo.dto.CategoryDto;
import com.goldloan.demo.entity.Category;
import com.goldloan.demo.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAllActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderBySortOrder();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }

    public Category saveCategory(CategoryDto categoryDto) {
        Category category = Category.builder()
            .name(categoryDto.getName())
            .description(categoryDto.getDescription())
            .imageUrl(categoryDto.getImageUrl())
            .sortOrder(categoryDto.getSortOrder())
            .isActive(true)
            .build();

        return categoryRepository.save(category);
    }
}
