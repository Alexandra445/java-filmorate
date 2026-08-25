package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.model.Director;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final DirectorStorage directorStorage;

    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("genreDbStorage") GenreStorage genreStorage,
            @Qualifier("mpaDbStorage") MpaStorage mpaStorage,
            @Qualifier("directorDbStorage") DirectorStorage directorStorage
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.directorStorage = directorStorage;
    }

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

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание больше 200 символов");
        }

        if (film.getReleaseDate() == null
                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза некорректна");
        }

        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }

        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new ValidationException("MPA должен быть указан");
        }

        if (mpaStorage.findById(film.getMpa().getId()) == null) {
            throw new NotFoundException("Рейтинг MPA не найден");
        }

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {

                if (genre.getId() == null) {
                    throw new ValidationException("Id жанра должен быть указан");
                }

                if (genreStorage.findById(genre.getId()) == null) {
                    throw new NotFoundException("Жанр не найден");
                }
            }
        }

        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {

                if (director.getId() == null) {
                    throw new ValidationException("Id режиссёра должен быть указан");
                }

                if (directorStorage.findById(director.getId()) == null) {
                    throw new NotFoundException("Режиссёр не найден");
                }
            }
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

        filmStorage.addLike(filmId, userId);

        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }


    public void removeLike(Integer filmId, Integer userId) {

        Film film = filmStorage.findById(filmId);

        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }

        if (userStorage.findById(userId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        filmStorage.removeLike(filmId, userId);

        log.info("Пользователь {} убрал лайк с фильма {}", userId, filmId);
    }

    public Collection<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        if (count == null || count <= 0) {
            throw new ValidationException("Количество популярных фильмов должно быть больше 0");
        }

        if (year != null && year < 1895) {
            throw new ValidationException("Год должен быть не меньше 1895");
        }

        return filmStorage.getPopularFilms(count, genreId, year);
    }

    public Collection<Film> getCommonFilms(Integer userId, Integer friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public void deleteFilm(Integer id) {
        getFilm(id);
        filmStorage.delete(id);
    }


    public void delete(Integer id) {
        filmStorage.delete(id);
    }

    public Collection<Film> getFilmsByDirector(Integer directorId, String sortBy) {

        if (directorStorage.findById(directorId) == null) {
            throw new NotFoundException("Режиссёр не найден");
        }

        if (!"year".equals(sortBy) && !"likes".equals(sortBy)) {
            throw new ValidationException("sortBy должен быть year или likes");
        }

        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }

    public Collection<Film> searchFilms(String query, String by) {

        if (query == null || query.isBlank()) {
            throw new ValidationException("Поисковый запрос не должен быть пустым");
        }

        if (!"title".equals(by)
                && !"director".equals(by)
                && !"director,title".equals(by)
                && !"title,director".equals(by)) {
            throw new ValidationException(
                    "Параметр by должен быть title, director или director,title"
            );
        }

        return filmStorage.searchFilms(query, by);
    }
}