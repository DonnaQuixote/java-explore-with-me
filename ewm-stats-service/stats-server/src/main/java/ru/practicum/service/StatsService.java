package ru.practicum.service;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    EndpointHitDto postHit(EndpointHitDto dto);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique);
}
