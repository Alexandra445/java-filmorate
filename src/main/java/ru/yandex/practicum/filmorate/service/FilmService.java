package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {

        if (film.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        if (filmStorage.findById(film.getId()) == null) {
            throw new NotFoundException("Фильм не найден");
        }

        validateFilm(film);

        return filmStorage.update(film);
    }

    private void validateFilm(Film film) {

        if (film == null) {
            throw new ValidationException("Тело запроса не должно быть пустым");
        }

        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription() != null
                && film.getDescription().length() > 200) {
            throw new ValidationException("Описание больше 200 символов");
        }

        if (film.getReleaseDate() == null
                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза некорректна");
        }

        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }
    }

    public Film getFilm(Integer id) {

        Film film = filmStorage.findById(id);

        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }

        return film;
    }

    public void addLike(Integer filmId, Integer userId) {

        Film film = filmStorage.findById(filmId);

        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }

        if (userStorage.findById(userId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        film.getLikes().add(userId);

        log.info(
                "Пользователь {} поставил лайк фильму {}",
                userId,
                filmId
        );
    }


    public void removeLike(Integer filmId, Integer userId) {

        Film film = filmStorage.findById(filmId);

        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }

        if (userStorage.findById(userId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        film.getLikes().remove(userId);

        log.info(
                "Пользователь {} убрал лайк с фильма {}",
                userId,
                filmId
        );
    }

    public Collection<Film> getPopularFilms(Integer count) {

        return filmStorage.findAll()
                .stream()
                .sorted((film1, film2) ->
                        Integer.compare(
                                film2.getLikes().size(),
                                film1.getLikes().size()
                        ))
                .limit(count)
                .toList();
    }
}
