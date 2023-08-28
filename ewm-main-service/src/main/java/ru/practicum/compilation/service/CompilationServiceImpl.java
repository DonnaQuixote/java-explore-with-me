package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dao.CompilationRepository;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository repository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto postCompilation(NewCompilationDto compilationDto) {
        Set<Event> events = new HashSet<>();
        if (compilationDto.getEvents() != null)
            events = Set.copyOf(eventRepository.findAllById(compilationDto.getEvents()));

        return CompilationMapper.toCompilationDto(
                repository.save(CompilationMapper.toCompilation(compilationDto, events)));
    }

    @Override
    public void deleteCompilation(Long compId) {
        try {
            repository.deleteById(compId);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException(String.format("Compilation with id=%d was not found", compId));
        }
    }

    @Override
    public CompilationDto patchCompilation(Long compId, UpdateCompilationRequest request) {
        Compilation compilation = repository.findById(compId).orElseThrow(() ->
                new IllegalArgumentException(String.format("Compilation with id=%d was not found", compId)));

        if (request.getPinned() != null) compilation.setPinned(request.getPinned());
        if (request.getTitle() != null) compilation.setTitle(request.getTitle());
        if (request.getEvents() != null) compilation.setEvents(
                new HashSet<>(eventRepository.findAllById(request.getEvents())));

        return CompilationMapper.toCompilationDto(repository.save(compilation));
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        return CompilationMapper.toCompilationDto(repository.findById(compId).orElseThrow(() ->
                new IllegalArgumentException(String.format("Compilation with id=%d was not found", compId))));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;
        Pageable page = PageRequest.of(from / size, size);

        if (pinned != null) compilations = repository.findByPinned(pinned, page);
        else compilations = repository.findAll(page).getContent();

        return compilations.stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }
}