package ru.practicum.category.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Data
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)))
public class NewCategoryDto {
    @NotNull
    @Size(min = 1, max = 50)
    @NotBlank
    String name;
}