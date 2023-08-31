package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@RequiredArgsConstructor(onConstructor = @__(@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)))
@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UpdateCommentRequest {
    @NotNull
    @Size(min = 3, max = 3000)
    String text;
}