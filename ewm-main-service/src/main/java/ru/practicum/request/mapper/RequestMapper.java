package ru.practicum.request.mapper;

import ru.practicum.event.model.Event;
import ru.practicum.request.dto.RequestStatus;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RequestMapper {

    public static ParticipationRequestDto toRequestDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .created(request.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .event(request.getEvent().getId())
                .id(request.getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().toString())
                .build();
    }

    public static ParticipationRequest toRequest(User requester, Event event, RequestStatus status) {
        return ParticipationRequest.builder()
                .requester(requester)
                .event(event)
                .status(status)
                .created(LocalDateTime.now())
                .build();
    }
}