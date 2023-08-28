package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.event.model.EventState;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto postEvent(Long userId, NewEventDto eventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequest request);

    List<ParticipationRequestDto> getRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult patchRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest request);

    EventFullDto getEvent(Long eventId, HttpServletRequest request);

    List<? extends EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            Boolean onlyAvailable,
                                            String sort, Integer from,
                                            Integer size,
                                            HttpServletRequest httpServletRequest);

    List<? extends EventShortDto> getEvents(List<Long> users, List<EventState> states,
                                            List<Long> categories, LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto patchEvent(Long eventId, UpdateEventAdminRequest request);
}