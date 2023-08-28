package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.request.dto.RequestStatus;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository repository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto postRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException(String.format("User with id=%d was not found", userId)));

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new IllegalArgumentException(String.format("Event with id=%d was not found", eventId)));

        if (repository.findByRequester_IdAndEvent_Id(userId, eventId).isPresent())
            throw new DataIntegrityViolationException("Already requested");

        if (event.getInitiator().getId().equals(userId))
            throw new DataIntegrityViolationException("Initiator can't apply");

        if (!event.getState().equals(EventState.PUBLISHED))
            throw new DataIntegrityViolationException("Event not published");

        if (event.getParticipantLimit() != 0 &&
                event.getParticipantLimit() <= repository.countByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new DataIntegrityViolationException("Participant limit reached");
        }

        return RequestMapper.toRequestDto(repository.save(RequestMapper.toRequest(user, event,
                !event.getRequestModeration() || event.getParticipantLimit() == 0 ?
                RequestStatus.CONFIRMED : RequestStatus.PENDING)));
    }

    @Override
    public ParticipationRequestDto patchRequest(Long userId, Long requestId) {
        ParticipationRequest request = repository.findByRequester_IdAndId(userId, requestId).orElseThrow(() ->
                new IllegalArgumentException(String.format("Request with id=%d was not found", requestId)));
        request.setStatus(RequestStatus.CANCELED);
        repository.save(request);
        return RequestMapper.toRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException(String.format("User with id=%d was not found", userId)));

        return repository.findByRequester_Id(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }
}