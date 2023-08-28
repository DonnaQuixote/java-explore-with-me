package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto postCategory(NewCategoryDto newCatDto);

    void deleteCategory(Long catId);

    CategoryDto patchCategory(Long catId, NewCategoryDto newCatDto);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategory(Long catId);
}