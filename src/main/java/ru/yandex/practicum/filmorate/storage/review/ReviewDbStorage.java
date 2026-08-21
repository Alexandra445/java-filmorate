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
import java.util.Optional;

@Repository
@Qualifier("reviewDbStorage")
public class ReviewDbStorage extends BaseStorage<Review> implements ReviewStorage, ReviewEvaluationStorage {

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

    private static final String FIND_REVIEW_BY_ID = """
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
            WHERE r.id = ?;
            """;

    private static final String DELETE_EVALUATION = "DELETE FROM reviews_evaluation " +
            "WHERE review_id = ? AND user_id = ?;";
    private static final String DELETE_LIKE = "DELETE FROM reviews_evaluation " +
            "WHERE review_id = ? AND user_id = ? AND review_evaluation = TRUE;";
    private static final String DELETE_DISLIKE = "DELETE FROM reviews_evaluation " +
            "WHERE review_id = ? AND user_id = ? AND review_evaluation = FALSE;";

    private static final String PUT_EVALUATION = "INSERT INTO reviews_evaluation(review_id, user_id, review_evaluation) " +
            "VALUES(?, ?, ?);";


    public ReviewDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Review> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    @Override
    public Review addReview(Review review) {
        Long id = insert(ADD_REVIEW,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId());
        review.setId(id);
        return review;
    }

    @Override
    public Review uppdateReview(Review review) {
        update(UPDATE_REVIEW,
                review.getContent(),
                review.getIsPositive(),
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

    @Override
    public Optional<Review> findReviewById(Long reviewId) {
        return findAny(FIND_REVIEW_BY_ID, reviewId);
    }

    @Override
    public void putLike(Long reviewId, Long userId) {
        insertWithoutId(PUT_EVALUATION, reviewId, userId, true);
    }

    @Override
    public void putDislike(Long reviewId, Long userId) {
        insertWithoutId(PUT_EVALUATION, reviewId, userId, false);
    }

    @Override
    public boolean deleteLike(Long reviewID, Long userId) {
        return delete(DELETE_LIKE, reviewID, userId);
    }

    @Override
    public boolean deleteDislike(Long reviewId, Long userId) {
        return delete(DELETE_DISLIKE, reviewId, userId);
    }

    @Override
    public boolean deleteEvaluation(Long reviewId, Long userId) {
        return delete(DELETE_EVALUATION, reviewId, userId);
    }
}
