package ru.practicum.request.service;

import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto postRequest(Long userId, Long eventId);

    ParticipationRequestDto patchRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequests(Long userId);
}