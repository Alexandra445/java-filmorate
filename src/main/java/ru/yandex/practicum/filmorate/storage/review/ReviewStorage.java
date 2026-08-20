package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {

    Review addReview(Review review);

    Review uppdateReview(Review review);

    boolean deleteReview(Long reviewId);

    Collection<Review> findReviewsByFilmId(Long filmId, int count);

    Collection<Review> findAllReviews(int count);
}
