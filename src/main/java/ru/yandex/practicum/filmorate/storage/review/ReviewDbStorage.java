package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Collection;
import java.util.List;

@Repository
@Qualifier("reviewDbStorage")
public class ReviewDbStorage extends BaseStorage<Review> implements ReviewStorage {

    private static final String ADD_REVIEW = "INSERT INTO reviews(content, is_positive, user_id, film_id) " +
            "VALUES (?, ?, ?, ?)";

    public ReviewDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Review> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    @Override
    public Review addReview(Review review) {
        Long id = insert(ADD_REVIEW,
                review.getContent(),
                review.isPositive(),
                review.getUserId(),
                review.getFilmId());
        review.setId(id);
        return review;
    }

    @Override
    public Review uppdateReview(Review review) {
        return null;
    }

    @Override
    public boolean deleteReview(Long reviewId) {
        return false;
    }

    @Override
    public Collection<Review> findReviewsByFilmId(Long filmId, int count) {
        return List.of();
    }

    @Override
    public Collection<Review> findAllReviews(int count) {
        return List.of();
    }
}
