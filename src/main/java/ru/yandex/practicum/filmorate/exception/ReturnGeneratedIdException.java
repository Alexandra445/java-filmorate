package ru.yandex.practicum.filmorate.exception;

public class ReturnGeneratedIdException extends InternalServerError {
    public ReturnGeneratedIdException(String message) {
        super(message);
    }
}
