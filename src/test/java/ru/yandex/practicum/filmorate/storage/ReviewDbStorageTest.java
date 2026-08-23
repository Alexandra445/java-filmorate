package ru.yandex.practicum.filmorate.storage;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewEvaluationStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@ComponentScan
public class ReviewDbStorageTest {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final ReviewStorage reviewStorage;
    private final ReviewEvaluationStorage evaluationStorage;

    @Autowired
    public ReviewDbStorageTest(@Qualifier("userDbStorage") UserStorage userStorage,
                               @Qualifier("filmDbStorage") FilmStorage filmStorage,
                               ReviewStorage reviewStorage,
                               ReviewEvaluationStorage evaluationStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.reviewStorage = reviewStorage;
        this.evaluationStorage = evaluationStorage;
    }

    @Test
    public void addReviewTest() {
        User user1 = ObjectCreator.generateRandomUser();
        User user2 = ObjectCreator.generateRandomUser();
        Film film = ObjectCreator.generateRandomFilm();

        userStorage.create(user1);
        userStorage.create(user2);
        filmStorage.create(film);

        Review review = ObjectCreator.generateRandomReview(user1.getId(), film.getId());

        reviewStorage.addReview(review);
        assertNotNull(review.getId());
        assertEquals(1L, review.getId());
    }

    @Test
    public void updateReviewTest() {
        User user1 = ObjectCreator.generateRandomUser();
        Film film = ObjectCreator.generateRandomFilm();

        userStorage.create(user1);
        filmStorage.create(film);

        Review review = ObjectCreator.generateRandomReview(user1.getId(), film.getId());

        reviewStorage.addReview(review);
        Review reviewToUpdate = ObjectCreator.generateRandomReview(user1.getId(), film.getId());
        reviewToUpdate.setId(review.getId());
        reviewStorage.uppdateReview(reviewToUpdate);
        Review reviewAfterUpdate = reviewStorage.findReviewById(reviewToUpdate.getId()).orElse(null);

        assertNotNull(reviewAfterUpdate);

        assertEquals(reviewToUpdate, reviewAfterUpdate);
    }

    @Test
    public void deleteReviewTest() {
        User user1 = ObjectCreator.generateRandomUser();
        Film film = ObjectCreator.generateRandomFilm();

        userStorage.create(user1);
        filmStorage.create(film);

        Review review = ObjectCreator.generateRandomReview(user1.getId(), film.getId());

        Long id = reviewStorage.addReview(review).getId();

        assertTrue(reviewStorage.deleteReview(id));
        Review reviewAfterDelete = reviewStorage.findReviewById(id).orElse(null);
        Assertions.assertNull(reviewAfterDelete);
    }

    @Test
    public void findAllReviews() {
        User user1 = ObjectCreator.generateRandomUser();
        User user2 = ObjectCreator.generateRandomUser();
        Film film1 = ObjectCreator.generateRandomFilm();
        Film film2 = ObjectCreator.generateRandomFilm();

        userStorage.create(user1);
        userStorage.create(user2);
        filmStorage.create(film1);
        filmStorage.create(film2);

        Review review1 = ObjectCreator.generateRandomReview(user1.getId(), film1.getId());
        Review review2 = ObjectCreator.generateRandomReview(user2.getId(), film2.getId());

        reviewStorage.addReview(review1);
        reviewStorage.addReview(review2);
        Collection<Review> reviews = reviewStorage.findAllReviews(10);
        assertNotNull(reviews);
        assertTrue(reviews.size() >= 2);
    }

    @Test
    public void likeTest() {
        User user1 = ObjectCreator.generateRandomUser();
        User user2 = ObjectCreator.generateRandomUser();
        User user3 = ObjectCreator.generateRandomUser();
        Film film1 = ObjectCreator.generateRandomFilm();

        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(user3);
        filmStorage.create(film1);

        Review review1 = ObjectCreator.generateRandomReview(user1.getId(), film1.getId());

        reviewStorage.addReview(review1);
        evaluationStorage.putLike(review1.getId(), user2.getId().longValue());
        evaluationStorage.putLike(review1.getId(), user3.getId().longValue());
        review1 = reviewStorage.findReviewById(review1.getId()).orElse(null);
        assertNotNull(review1);
        assertEquals(2, review1.getUseful());

        evaluationStorage.deleteLike(review1.getId(), user2.getId().longValue());
        evaluationStorage.deleteLike(review1.getId(), user3.getId().longValue());
        review1 = reviewStorage.findReviewById(review1.getId()).orElse(null);
        assertNotNull(review1);
        assertEquals(0, review1.getUseful());

    }

    @Test
    public void dislikeTest() {
        User user1 = ObjectCreator.generateRandomUser();
        User user2 = ObjectCreator.generateRandomUser();
        User user3 = ObjectCreator.generateRandomUser();
        Film film1 = ObjectCreator.generateRandomFilm();

        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(user3);
        filmStorage.create(film1);

        Review review1 = ObjectCreator.generateRandomReview(user1.getId(), film1.getId());

        reviewStorage.addReview(review1);
        evaluationStorage.putDislike(review1.getId(), user2.getId().longValue());
        evaluationStorage.putDislike(review1.getId(), user3.getId().longValue());
        review1 = reviewStorage.findReviewById(review1.getId()).orElse(null);
        assertNotNull(review1);
        assertEquals(-2, review1.getUseful());

        reviewStorage.addReview(review1);
        evaluationStorage.deleteDislike(review1.getId(), user2.getId().longValue());
        evaluationStorage.deleteDislike(review1.getId(), user3.getId().longValue());
        review1 = reviewStorage.findReviewById(review1.getId()).orElse(null);
    }
}
