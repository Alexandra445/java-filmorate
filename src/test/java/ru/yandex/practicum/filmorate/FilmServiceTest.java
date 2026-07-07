package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    private FilmService service;


    @BeforeEach
    void setUp() {
        service = new FilmService(
                new InMemoryFilmStorage()
        );
    }


    @Test
    void shouldCreateValidFilm() {

        Film film = new Film();

        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(100);


        Film result = service.create(film);


        assertNotNull(result.getId());
    }


    @Test
    void shouldFailWhenNameEmpty() {

        Film film = new Film();

        film.setName("");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);


        assertThrows(
                ValidationException.class,
                () -> service.create(film)
        );
    }


    @Test
    void shouldFailWhenTooLongDescription() {

        Film film = new Film();

        film.setName("Film");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);


        assertThrows(
                ValidationException.class,
                () -> service.create(film)
        );
    }


    @Test
    void shouldFailWhenReleaseDateTooEarly() {

        Film film = new Film();

        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(100);


        assertThrows(
                ValidationException.class,
                () -> service.create(film)
        );
    }


    @Test
    void shouldFailWhenDurationZero() {

        Film film = new Film();

        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(0);


        assertThrows(
                ValidationException.class,
                () -> service.create(film)
        );
    }


    @Test
    void shouldFailWhenFilmIsNull() {

        assertThrows(
                ValidationException.class,
                () -> service.create(null)
        );
    }
}