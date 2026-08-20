package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Qualifier("filmMemoryStorage")
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {

        Film oldFilm = films.get(film.getId());

        oldFilm.setName(film.getName());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate());
        oldFilm.setDuration(film.getDuration());

        return oldFilm;
    }

    @Override
    public Film findById(Integer id) {
        return films.get(id);
    }

    private Integer getNextId() {
        int currentMaxId = films.keySet().stream().mapToInt(id -> id).max().orElse(0);

        return ++currentMaxId;
    }

    @Override
    public void delete(Integer id) {
        films.remove(id);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {

        Film film = films.get(filmId);

        if (film != null) {
            film.getLikes().add(userId);
        }
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {

        Film film = films.get(filmId);

        if (film != null) {
            film.getLikes().remove(userId);
        }
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        throw new UnsupportedOperationException("Not implemented for In-Memory storage");
    }

        @Override
    public Collection<Film> getCommonFilms(Integer userId, Integer friendId) {
        throw new UnsupportedOperationException("Not implemented for In-Memory storage");
    }
}