package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorService {

    @Qualifier("directorDbStorage")
    private final DirectorStorage directorStorage;

    public Collection<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director findById(Integer id) {

        Director director = directorStorage.findById(id);

        if (director == null) {
            throw new NotFoundException("Режиссёр не найден");
        }

        return director;
    }

    public Director create(Director director) {

        validateDirector(director);

        return directorStorage.add(director);
    }

    public Director update(Director director) {

        if (director.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        if (directorStorage.findById(director.getId()) == null) {
            throw new NotFoundException("Режиссёр не найден");
        }

        validateDirector(director);

        return directorStorage.update(director);
    }

    public void delete(Integer id) {

        if (directorStorage.findById(id) == null) {
            throw new NotFoundException("Режиссёр не найден");
        }

        directorStorage.delete(id);
    }

    private void validateDirector(Director director) {

        if (director == null) {
            throw new ValidationException("Тело запроса не должно быть пустым");
        }

        if (director.getName() == null || director.getName().isBlank()) {
            throw new ValidationException("Имя режиссёра не может быть пустым");
        }
    }
}