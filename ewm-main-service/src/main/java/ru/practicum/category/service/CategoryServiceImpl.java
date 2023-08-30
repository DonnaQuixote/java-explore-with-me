package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;

    @Override
    public CategoryDto postCategory(NewCategoryDto newCatDto) {
        return CategoryMapper.toCategoryDto(
                repository.save(CategoryMapper.toCategory(newCatDto)));
    }

    @Override
    public void deleteCategory(Long catId) {
        try {
            repository.deleteById(catId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(
                    String.format("Category with id=%d was not found", catId));
        }
    }

    @Override
    public CategoryDto patchCategory(Long catId,
                                     NewCategoryDto newCatDto) {
        if (!repository.existsById(catId))
            throw new EntityNotFoundException(
                    String.format("Category with id=%d was not found", catId));

        return CategoryMapper.toCategoryDto(repository.save(
                new Category(catId, newCatDto.getName())));
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        return repository
                .findAll(PageRequest.of(from / size, size))
                .map(CategoryMapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        return CategoryMapper
                .toCategoryDto(repository.findById(catId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                String.format("Category with id=%d was not found", catId))));
    }
}