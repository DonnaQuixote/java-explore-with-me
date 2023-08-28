package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.RequestService;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@Slf4j
@Validated
public class RequestController {
    private final RequestService service;

    @GetMapping
    public List<ParticipationRequestDto> getRequests(@PathVariable @Positive Long userId) {
        log.debug("GET requests for user with id: {}", userId);
        return service.getRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto postRequest(@PathVariable @Positive Long userId,
                                               @RequestParam @Positive Long eventId) {
        log.debug("POST request for userId: {} and eventId: {}", userId, eventId);
        return service.postRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto patchRequest(@PathVariable @Positive Long userId,
                                                @PathVariable @Positive Long requestId) {
        log.debug("PATCH request for userId: {} and requestId: {}", userId, requestId);
        return service.patchRequest(userId, requestId);
    }
}