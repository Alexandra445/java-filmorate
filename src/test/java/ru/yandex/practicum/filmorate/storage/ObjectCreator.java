package ru.yandex.practicum.filmorate.storage;

import org.apache.commons.lang3.RandomStringUtils;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;

public class ObjectCreator {

    public static User generateRandomUser() {
        User user = new User();
        user.setEmail(RandomStringUtils.randomAlphanumeric(8) + "@test.com");
        user.setLogin(RandomStringUtils.randomAlphabetic(8));
        user.setName(RandomStringUtils.randomAlphabetic(10));
        user.setBirthday(LocalDate.of(2000, 5, 10));
        return user;
    }

    public static Film generateRandomFilm() {
        Film film = new Film();
        film.setName(RandomStringUtils.randomAlphabetic(10));
        film.setDescription(RandomStringUtils.randomAlphabetic(40));
        film.setReleaseDate(LocalDate.of(2000, 10, 1));
        film.setDuration(100);
        Mpa rating = new Mpa();
        rating.setId(2);
        film.setMpa(rating);
        film.setGenres(new HashSet<>());
        return film;
    }

    public static Review generateRandomReview(long userId, long filmId) {
        Review review = new Review();
        review.setContent(RandomStringUtils.randomAlphabetic(20));
        review.setIsPositive(false);
        review.setUserId(userId);
        review.setFilmId(filmId);

        return review;
    }
}
