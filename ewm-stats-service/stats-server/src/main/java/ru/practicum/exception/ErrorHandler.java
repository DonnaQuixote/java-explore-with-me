package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.StatsController;

import java.time.DateTimeException;
import java.util.Map;

@RestControllerAdvice(basePackageClasses = StatsController.class)
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> handleWrongDateTime(final DateTimeException e) {
        return Map.of("ERROR", e.getMessage());
    }
}