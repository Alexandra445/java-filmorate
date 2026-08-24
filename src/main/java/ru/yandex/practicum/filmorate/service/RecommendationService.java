package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.recommendation.RecommendationStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationStorage recommendationStorage;

    public Collection<Film> findRecommendationsForUser(Long userId) {
        log.debug("Поиск рекомендаций по фильмам для пользователя с id {}", userId);
        return recommendationStorage.getRecommendedFilms(userId);
    }
}
