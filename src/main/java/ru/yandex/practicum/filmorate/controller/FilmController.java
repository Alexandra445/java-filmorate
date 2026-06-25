package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validateFilm(film);

        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Добавлен фильм: {}", film);

        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        validateFilm(film);

        if (film.getId() == null) {
            log.error("Не указан id фильма");
            throw new ValidationException("Id должен быть указан");
        }

        films.put(film.getId(), film);

        log.info("Обновлён фильм: {}", film);

        return film;
    }

    private void validateFilm(Film film) {

        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма пустое");
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription() != null
                && film.getDescription().length() > 200) {
            log.error("Описание фильма превышает 200 символов");
            throw new ValidationException("Описание больше 200 символов");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Некорректная дата релиза");
            throw new ValidationException("Дата релиза некорректна");
        }

        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность должна быть положительной");
        }
    }

    private Integer getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}