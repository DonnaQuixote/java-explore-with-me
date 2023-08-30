package ru.practicum.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Data
@Builder
public class NewUserRequest {
    @NotNull
    @Email
    @Size(min = 6, max = 254)
    String email;
    Long id;
    @NotNull
    @NotBlank
    @Size(min = 2, max = 250)
    String name;
}