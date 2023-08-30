package ru.practicum.exception;

public class EditingProhibitedException extends RuntimeException {
    public EditingProhibitedException(String message) {
        super(message);
    }
}
