package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.recommendation.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class RecommendationService {

    private final RecommendationStorage recommendationStorage;
    private final UserStorage userStorage;

    public RecommendationService(RecommendationStorage recommendationStorage,
                                 @Qualifier("userDbStorage") UserStorage userStorage) {
        this.recommendationStorage = recommendationStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findRecommendationsForUser(Long userId) {
        if (userStorage.findById(userId.intValue()) == null) {
            log.debug("Пользователя с id {} не существует", userId);
            throw new UserNotFoundException("Пользователь не существует");
        }
        log.debug("Поиск рекомендаций по фильмам для пользователя с id {}", userId);
        return recommendationStorage.getRecommendedFilms(userId);
    }
}
