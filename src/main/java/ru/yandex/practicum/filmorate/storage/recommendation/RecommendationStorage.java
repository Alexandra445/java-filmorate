package ru.yandex.practicum.filmorate.storage.recommendation;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface RecommendationStorage {
    Collection<Film> getRecommendedFilms(Long userId);
}
