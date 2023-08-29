package ru.practicum.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Data
@Builder
public class UserDto {
    String email;
    Long id;
    String name;
}
