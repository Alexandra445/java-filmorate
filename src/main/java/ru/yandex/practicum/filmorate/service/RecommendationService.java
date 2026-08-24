package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.recommendation.RecommendationStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationStorage recommendationStorage;

    public Collection<Film> findRecommendationsForUser(Long userId) {
        return recommendationStorage.getRecommendedFilms(userId);
    }
}
