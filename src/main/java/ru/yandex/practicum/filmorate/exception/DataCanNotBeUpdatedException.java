package ru.yandex.practicum.filmorate.exception;

public class DataCanNotBeUpdatedException extends InternalServerError {
    public DataCanNotBeUpdatedException(String message) {
        super(message);
    }
}
