package ru.practicum.compilation.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor(onConstructor = @__(@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)))
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class NewCompilationDto {
    List<Long> events;
    boolean pinned;
    @NotNull
    @Size(min = 1, max = 50)
    @NotBlank
    String title;
}