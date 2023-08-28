package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.ViewStats;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.request.RequestMapper;
import ru.practicum.request.RequestStatus;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventFullDto postEvent(Long userId, NewEventDto eventDto) {
        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2)))
            throw new DateTimeException(
                    "Field: eventDate. Error: before the event less than two hours. Value: " + eventDto.getEventDate());

        User user = userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException(String.format("User with id=%d was not found", userId)));

        Category category = categoryRepository.findById(eventDto.getCategory()).orElseThrow(() ->
                new IllegalArgumentException(
                        String.format("Category with id=%d was not found", eventDto.getCategory())));

        return EventMapper.toEventFullDto(repository.save(EventMapper.toEvent(eventDto, user, category)));
    }

    public EventFullDto getEvent(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException(String.format("User with id=%d was not found", userId)));

        Event event = repository.findById(eventId).orElseThrow(() ->
                new IllegalArgumentException(String.format("Event with id=%d was not found", eventId)));

        return setStats(event);
    }

    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException(String.format("User with id=%d was not found", userId)));

        List<Event> events = repository.findByInitiator(user, PageRequest.of(from / size, size));

        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    public EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException(String.format("User with id=%d was not found", userId)));

        Event event = repository.findByIdAndInitiator_Id(eventId, userId).orElseThrow(() ->
                new IllegalArgumentException(String.format("Event with id=%d was not found", eventId)));

        if (event.getState() == EventState.PUBLISHED) throw new ValidationException(
                "Only pending or canceled events can be changed");

        if (request.getStateAction() != null) {
            event.setState(request.getStateAction() == StateAction.SEND_TO_REVIEW ?
                    EventState.PENDING : EventState.CANCELED);
        }

        return patch(event, request, false);
    }

    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException(String.format("User with id=%d was not found", userId)));

        repository.findByIdAndInitiator_Id(eventId, userId).orElseThrow(() ->
                new IllegalArgumentException(String.format("Event with id=%d was not found", eventId)));

        return requestRepository.findByEvent_Id(eventId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    public EventRequestStatusUpdateResult patchRequest(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest request) {
        userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException(String.format("User with id=%d was not found", userId)));

        Event event = repository.findByIdAndInitiator_Id(eventId, userId).orElseThrow(() ->
                new IllegalArgumentException(String.format("Event with id=%d was not found", eventId)));

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) throw new ValidationException(
                "No moderation needed");

        long slots = event.getParticipantLimit() - requestRepository.countByEvent_IdAndStatus(eventId,
                RequestStatus.CONFIRMED);
        if (slots <= 0) throw new ValidationException("The participant limit has been reached");

        List<ParticipationRequest> requests = requestRepository.findAllById(request.getRequestIds());

        for(ParticipationRequest req : requests) {
            if (request.getStatus() == RequestStatus.CONFIRMED && slots > 0) {
                req.setStatus(RequestStatus.CONFIRMED);
                slots--;
            } else if (request.getStatus() == RequestStatus.REJECTED) {
                req.setStatus(RequestStatus.REJECTED);
            }
        }
        requestRepository.saveAll(requests);

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(requestRepository.findByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED).stream()
                        .map(RequestMapper::toRequestDto).collect(Collectors.toList()))
                .rejectedRequests(requestRepository.findByEvent_IdAndStatus(eventId, RequestStatus.REJECTED).stream()
                        .map(RequestMapper::toRequestDto).collect(Collectors.toList())).build();
    }

    public EventFullDto getEvent(Long eventId, HttpServletRequest httpServletRequest) {
        Event event = repository.findById(eventId).orElseThrow(() ->
                new IllegalArgumentException(String.format("Event with id=%d was not found", eventId)));

        if (event.getState() != EventState.PUBLISHED) throw new IllegalArgumentException(
                String.format("Event with id=%d was not found", eventId));

        saveHit(httpServletRequest);

        return setStats(event);
    }

    public List<? extends EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,
                                              LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd,
                                              Boolean onlyAvailable,
                                              String sort, Integer from,
                                              Integer size,
                                              HttpServletRequest httpServletRequest) {
        if (rangeStart == null) rangeStart = LocalDateTime.now();
        if (rangeEnd == null) rangeEnd = LocalDateTime.now().plusYears(100);

        if (rangeStart.isAfter(rangeEnd)) throw new DateTimeException("The end can't be before the start");

        List<EventShortDto> events = repository.search(text, categories, paid,
                        rangeStart, rangeEnd, onlyAvailable,
                PageRequest.of(from / size, size))
                .stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());

        boolean viewsSort = false;
        if (sort != null) {
            if (sort.equals("EVENT_DATE")) {
                events = events.stream()
                        .sorted(Comparator.comparing(EventShortDto::getEventDate))
                        .collect(Collectors.toList());
            } else viewsSort = true;
        }

        saveHit(httpServletRequest);
        return viewsSort ? setStats(events, rangeStart, rangeEnd).stream()
                .sorted(Comparator.comparingLong(EventShortDto::getViews))
                .collect(Collectors.toList()) : setStats(events, rangeStart, rangeEnd);
    }

    public List<? extends EventShortDto> getEvents(List<Long> users, List<EventState> states,
                                        List<Long> categories, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Integer from, Integer size) {
        if (rangeStart == null) rangeStart = LocalDateTime.now();
        if (rangeEnd == null) rangeEnd = LocalDateTime.now().plusYears(100);

        Pageable page = PageRequest.of(from / size, size);
        List<EventFullDto> events = repository.adminSearch(users, states, categories, rangeStart, rangeEnd, page)
                .stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());

        return setStats(events, rangeStart, rangeEnd);
    }

    public EventFullDto patchEvent(Long eventId, UpdateEventAdminRequest request) {
        Event event = repository.findById(eventId).orElseThrow(() ->
                new IllegalArgumentException(String.format("Event with id=%d was not found", eventId)));

        if (request.getStateAction() != null) {
            if (event.getState() == EventState.PUBLISHED || event.getState() == EventState.CANCELED)
                throw new ValidationException("Already published or canceled");
            if (request.getStateAction() == AdminStateAction.PUBLISH_EVENT) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else event.setState(EventState.CANCELED);
        }

        return patch(event, request, true);
    }

    public EventFullDto patch(Event event, UpdateEvent request, boolean isAdmin) {
        if (request.getEventDate() != null) {
            if (request.getEventDate().isBefore(LocalDateTime.now().plusHours(isAdmin ? 1 : 2)))
                throw new DateTimeException(
                    "Field: eventDate. Error: Event too soon. Value: " + request.getEventDate());
            event.setEventDate(request.getEventDate());
        }

        if (!isAdmin) if (request.getLocation() != null) event.setLocation(request.getLocation());

        if (request.getAnnotation() != null) event.setAnnotation(request.getAnnotation());
        if (request.getCategory() != null) event.setCategory(categoryRepository.findById(request.getCategory())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Category with id=%d was not found",
                        request.getCategory()))));
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getPaid() != null) event.setPaid(request.getPaid());
        if (request.getParticipantLimit() != null) event.setParticipantLimit(request.getParticipantLimit());
        if (request.getRequestModeration() != null) event.setRequestModeration(request.getRequestModeration());
        if (request.getTitle() != null) event.setTitle(request.getTitle());
        return EventMapper.toEventFullDto(repository.save(event));
    }

    private void saveHit(HttpServletRequest httpServletRequest) {
        statsClient.postHit(EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(httpServletRequest.getRequestURI())
                .ip(httpServletRequest.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }

    private EventFullDto setStats(Event event) {
        List<ViewStats> stats = statsClient.getStats(
                event.getCreatedOn().format(FORMATTER),
                LocalDateTime.now().format(FORMATTER),
                new String[]{String.format("/events/%d", event.getId())},
                true);

        EventFullDto dto = EventMapper.toEventFullDto(event);
        dto.setViews(stats.size() == 0 ? 0 : stats.get(0).getHits());
        return dto;
    }

    private List<? extends EventShortDto> setStats(List<? extends EventShortDto> events,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd) {
        Map<Long, ? extends EventShortDto> dtoMap = events.stream()
                .collect(Collectors.toMap(EventShortDto::getId, Function.identity()));

        Map<String, Long> uris = new HashMap<>();
        dtoMap.keySet().forEach(id -> uris.put(String.format("/events/%d", id), id));

        List<ViewStats> stats = statsClient.getStats(
                rangeStart.format(FORMATTER),
                rangeEnd.format(FORMATTER),
                uris.keySet().toArray(new String[0]),
                true);

        for (ViewStats stat : stats) {
            if (uris.containsKey(stat.getUri()))
                dtoMap.get(uris.get(stat.getUri())).setViews((stat.getHits()));
        }

        return new ArrayList<>(dtoMap.values());
    }
}