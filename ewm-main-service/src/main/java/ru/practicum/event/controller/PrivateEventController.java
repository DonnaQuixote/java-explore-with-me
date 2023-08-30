package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.service.EventService;
import ru.practicum.event.dto.*;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {
    private final EventService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postEvent(@PathVariable @Positive Long userId,
                                  @Valid @RequestBody NewEventDto eventDto) {
        log.debug("POST user event: {}", eventDto);
        return service.postEvent(userId, eventDto);
    }

    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable @Positive Long userId,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {
        log.debug("GET user events, id: {}", userId);
        return service.getEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable @Positive Long userId,
                                 @PathVariable @Positive Long eventId) {
        log.debug("GET event with id: {}", eventId);
        return service.getEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEvent(@PathVariable @Positive Long userId,
                                   @PathVariable @Positive Long eventId,
                                   @Valid @RequestBody UpdateEventUserRequest request) {
        log.debug("PATCH event (user): {}", request);
        return service.patchEvent(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable @Positive Long userId,
                                               @PathVariable @Positive Long eventId) {
        log.debug("GET requests, user: {}, event: {}", userId, eventId);
        return service.getRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult patchRequest(@PathVariable @Positive Long userId,
                                                        @PathVariable @Positive Long eventId,
                                                        @RequestBody @Valid EventRequestStatusUpdateRequest request) {
        log.debug("PATCH requests, user: {}, event: {}, request: {}", userId, eventId, request);
        return service.patchRequest(userId, eventId, request);
    }
}