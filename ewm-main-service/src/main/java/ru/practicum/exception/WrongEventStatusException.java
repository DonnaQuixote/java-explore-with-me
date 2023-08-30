package ru.practicum.exception;

public class WrongEventStatusException extends RuntimeException {
    public WrongEventStatusException(String message) {
        super(message);
    }
}
