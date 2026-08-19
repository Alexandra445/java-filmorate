package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Film findById(Integer id);

    void delete(Integer id);

    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);

    Collection<Film> getPopularFilms(Integer count);

    Collection<Film> getCommonFilms(Integer userId, Integer friendId);
}