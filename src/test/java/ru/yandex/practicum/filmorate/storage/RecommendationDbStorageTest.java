package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.recommendation.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;


@SpringBootTest
@AutoConfigureTestDatabase
@ComponentScan
public class RecommendationDbStorageTest {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final RecommendationStorage recommendationStorage;

    @Autowired
    public RecommendationDbStorageTest(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                                       @Qualifier("userDbStorage") UserStorage userStorage,
                                       RecommendationStorage recommendationStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.recommendationStorage = recommendationStorage;
    }

    @Test
    public void getRecommendationTest() {
        User user1 = ObjectCreator.generateRandomUser();
        User user2 = ObjectCreator.generateRandomUser();

        Film film1 = ObjectCreator.generateRandomFilm();
        Film film2 = ObjectCreator.generateRandomFilm();
        Film film3 = ObjectCreator.generateRandomFilm();
        Film film4 = ObjectCreator.generateRandomFilm();
        Film film5 = ObjectCreator.generateRandomFilm();

        filmStorage.create(film1);
        filmStorage.create(film2);
        filmStorage.create(film3);
        filmStorage.create(film4);
        filmStorage.create(film5);

        userStorage.create(user1);
        userStorage.create(user2);

        filmStorage.addLike(film1.getId(), user1.getId());
        filmStorage.addLike(film2.getId(), user1.getId());

        filmStorage.addLike(film1.getId(), user2.getId());
        filmStorage.addLike(film2.getId(), user2.getId());
        filmStorage.addLike(film3.getId(), user2.getId());
        filmStorage.addLike(film4.getId(), user2.getId());

        Collection<Film> films = recommendationStorage.getRecommendedFilms(user1.getId().longValue());
        System.out.println(films);


    }

}
