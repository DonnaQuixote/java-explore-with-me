package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.Query;
import ru.practicum.event.service.EventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventController {
    private final EventService service;

    @GetMapping
    public List<? extends EventShortDto> getEvents(@RequestParam(required = false) String text,
                                                   @RequestParam(required = false) List<Long> categories,
                                                   @RequestParam(required = false) Boolean paid,
                                                   @RequestParam(required = false)
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                       LocalDateTime rangeStart,
                                                   @RequestParam(required = false)
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                       LocalDateTime rangeEnd,
                                                   @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                   @RequestParam(required = false) String sort,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "10") Integer size,
                                              HttpServletRequest httpServletRequest) {
        log.debug("GET events (public)");
        return service.getEventsPublic(
                Query.builder()
                        .text(text)
                        .categories(categories)
                        .paid(paid)
                        .rangeStart(rangeStart)
                        .rangeEnd(rangeEnd)
                        .onlyAvailable(onlyAvailable)
                        .sort(sort)
                        .from(from)
                        .size(size)
                        .request(httpServletRequest)
                        .build());
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@Positive @PathVariable Long id, HttpServletRequest httpServletRequest) {
        log.debug("GET event (public) with id: {}", id);
        return service.getEvent(id, httpServletRequest);
    }
}