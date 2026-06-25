package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController controller;

    @BeforeEach
    void setUp() {
        controller = new FilmController();
    }

    @Test
    void shouldCreateValidFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("A".repeat(200));
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(100);

        Film result = controller.create(film);

        assertNotNull(result.getId());
    }

    @Test
    void shouldFailWhenNameEmpty() {
        Film film = new Film();
        film.setName("");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);

        assertThrows(ValidationException.class,
                () -> controller.create(film));
    }

    @Test
    void shouldFailWhenTooLongDescription() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);

        assertThrows(ValidationException.class,
                () -> controller.create(film));
    }

    @Test
    void shouldFailWhenReleaseDateTooEarly() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(100);

        assertThrows(ValidationException.class,
                () -> controller.create(film));
    }

    @Test
    void shouldFailWhenDurationZero() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(0);

        assertThrows(ValidationException.class,
                () -> controller.create(film));
    }
}
