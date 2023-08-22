package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStats;
import ru.practicum.dao.StatsRepository;
import ru.practicum.mapper.HitMapper;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    @Override
    public EndpointHitDto postHit(EndpointHitDto dto) {
        return HitMapper.toEndpointHitDto(repository.save(HitMapper.toEndpointHit(dto)));
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        if (start.isAfter(end)) throw new DateTimeException("Конец статистики не может быть раньше начала");
        if (unique) {
           if (uris != null) return repository.getUniqueStats(start, end, uris);
           else return repository.getUniqueWithoutUris(start, end);
        }
        if (uris == null) return repository.getWithoutUris(start, end);
        return repository.getStats(start, end, uris);
    }
}