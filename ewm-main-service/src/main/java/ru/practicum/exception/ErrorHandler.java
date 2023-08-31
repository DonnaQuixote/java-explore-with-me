package ru.practicum.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ApiError handleNotValidArg(final MethodArgumentNotValidException e) {
        return createError(e, BAD_REQUEST,
                Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage(),
                "Incorrectly made request.");
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public ApiError handleConstraintViolation(final DataIntegrityViolationException e) {
        return createError(e, CONFLICT, e.getMessage(), "Integrity constraint has been violated.");
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ApiError handleBadQuery(final NumberFormatException e) {
        return createError(e, BAD_REQUEST, e.getMessage(), "Incorrectly made request.");
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ApiError handleNotFound(final EntityNotFoundException e) {
        return createError(e, NOT_FOUND, e.getMessage(), "The required object was not found.");
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ApiError handleWrongDateTime(final DateTimeException e) {
        return createError(e, BAD_REQUEST, e.getMessage(),
                "For the requested operation the conditions are not met.");
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public ApiError handleEditingProhibited(final EditingProhibitedException e) {
        return createError(e, CONFLICT, e.getMessage(),
                "For the requested operation the conditions are not met.");
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public ApiError handleWrongStatus(final WrongEventStatusException e) {
        return createError(e, CONFLICT, e.getMessage(),
                "For the requested operation the conditions are not met.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleRuntimeException(final RuntimeException e) {
        return createError(e, INTERNAL_SERVER_ERROR, e.getMessage(), "Something went wrong!");
    }

    private ApiError createError(Exception e, HttpStatus status, String message, String reason) {
        return new ApiError(
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList()),
                status,
                reason,
                message,
                LocalDateTime.now());
    }
}