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

    private static final String UPDATE_REVIEW = "UPDATE reviews SET content = ?, is_positive = ?" +
            "WHERE id = ?";
    private static final String DELETE_REVIEW = "DELETE FROM reviews WHERE id = ?";

    private static final String FIND_REVIEWS_BY_FILM = """
            SELECT r.*,
                   COALESCE(likes.cnt, 0) - COALESCE(dislikes.cnt, 0) AS rating
            FROM reviews r
            LEFT JOIN (
                SELECT review_id, COUNT(*) AS cnt
                FROM reviews_evaluation
                WHERE review_evaluation = TRUE
                GROUP BY review_id
            ) likes ON likes.review_id = r.id
            LEFT JOIN (
                SELECT review_id, COUNT(*) AS cnt
                FROM reviews_evaluation
                WHERE review_evaluation = FALSE
                GROUP BY review_id
            ) dislikes ON dislikes.review_id = r.id
            WHERE r.film_id = ?
            ORDER BY rating DESC
            LIMIT ?;
            """;

    private static final String FIND_ALL_REVIEWS = """
            SELECT r.*, COALESCE(likes.cnt, 0) - COALESCE(dislikes.cnt, 0) AS rating
            FROM reviews AS r
            LEFT JOIN (
                SELECT review_id, COUNT(*) AS cnt
                FROM reviews_evaluation
                WHERE review_evaluation = TRUE
                GROUP BY review_id
            ) AS likes ON likes.review_id = r.id
            LEFT JOIN (
                SELECT review_id, COUNT(*) AS cnt
                FROM reviews_evaluation
                WHERE review_evaluation = FALSE
                GROUP BY review_id
            ) AS dislikes ON dislikes.review_id = r.id
            ORDER BY rating DESC
            LIMIT ?;
            """;


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
        update(UPDATE_REVIEW,
                review.getContent(),
                review.isPositive(),
                review.getId());
        return review;
    }

    @Override
    public boolean deleteReview(Long reviewId) {
        return delete(DELETE_REVIEW, reviewId);
    }

    @Override
    public Collection<Review> findReviewsByFilmId(Long filmId, int count) {
        return findMany(FIND_REVIEWS_BY_FILM, filmId, count);
    }

    @Override
    public Collection<Review> findAllReviews(int count) {
        return findMany(FIND_ALL_REVIEWS, count);
    }
}
