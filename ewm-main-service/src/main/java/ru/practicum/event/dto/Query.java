package ru.practicum.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.practicum.event.model.EventState;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class Query {
    List<Long> users;
    List<EventState> states;
    String text;
    List<Long> categories;
    Boolean paid;
    @NonFinal
    LocalDateTime rangeStart;
    @NonFinal
    LocalDateTime rangeEnd;
    Boolean onlyAvailable;
    String sort;
    Integer from;
    Integer size;
    HttpServletRequest request;
}