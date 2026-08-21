package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Optional;

@Repository
@Qualifier("reviewDbStorage")
public class ReviewDbStorage implements ReviewStorage, ReviewEvaluationStorage {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Review> rowMapper;

    private static final String ADD_REVIEW =
            "INSERT INTO reviews(content, is_positive, user_id, film_id) " +
                    "VALUES (?, ?, ?, ?)";

    private static final String UPDATE_REVIEW =
            "UPDATE reviews SET content = ?, is_positive = ? " +
                    "WHERE id = ?";

    private static final String DELETE_REVIEW =
            "DELETE FROM reviews WHERE id = ?";

    private static final String FIND_REVIEWS_BY_FILM = """
            SELECT r.*,
                   COALESCE(
                       (
                           SELECT SUM(
                               CASE
                                   WHEN re.review_evaluation = TRUE THEN 1
                                   ELSE -1
                               END
                           )
                           FROM reviews_evaluation re
                           WHERE re.review_id = r.id
                       ),
                       0
                   ) AS rating
            FROM reviews r
            WHERE r.film_id = ?
            ORDER BY rating DESC
            LIMIT ?
            """;

    private static final String FIND_ALL_REVIEWS = """
            SELECT r.*,
                   COALESCE(
                       (
                           SELECT SUM(
                               CASE
                                   WHEN re.review_evaluation = TRUE THEN 1
                                   ELSE -1
                               END
                           )
                           FROM reviews_evaluation re
                           WHERE re.review_id = r.id
                       ),
                       0
                   ) AS rating
            FROM reviews r
            ORDER BY rating DESC
            LIMIT ?
            """;

    private static final String FIND_REVIEW_BY_ID = """
            SELECT r.*,
                   COALESCE(
                       (
                           SELECT SUM(
                               CASE
                                   WHEN re.review_evaluation = TRUE THEN 1
                                   ELSE -1
                               END
                           )
                           FROM reviews_evaluation re
                           WHERE re.review_id = r.id
                       ),
                       0
                   ) AS rating
            FROM reviews r
            WHERE r.id = ?
            """;

    private static final String DELETE_EVALUATION =
            "DELETE FROM reviews_evaluation " +
                    "WHERE review_id = ? AND user_id = ?";

    private static final String DELETE_LIKE =
            "DELETE FROM reviews_evaluation " +
                    "WHERE review_id = ? AND user_id = ? " +
                    "AND review_evaluation = TRUE";

    private static final String DELETE_DISLIKE =
            "DELETE FROM reviews_evaluation " +
                    "WHERE review_id = ? AND user_id = ? " +
                    "AND review_evaluation = FALSE";

    private static final String PUT_EVALUATION =
            "INSERT INTO reviews_evaluation(" +
                    "review_id, user_id, review_evaluation) " +
                    "VALUES (?, ?, ?)";

    public ReviewDbStorage(JdbcTemplate jdbcTemplate,
                           RowMapper<Review> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public Review addReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(
                    ADD_REVIEW,
                    new String[]{"id"}
            );

            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());

            return stmt;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            review.setReviewId(keyHolder.getKey().longValue());
        }

        return review;
    }

    @Override
    public Review uppdateReview(Review review) {
        jdbcTemplate.update(
                UPDATE_REVIEW,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );

        return review;
    }

    @Override
    public boolean deleteReview(Long reviewId) {
        int rows = jdbcTemplate.update(
                DELETE_REVIEW,
                reviewId
        );

        return rows > 0;
    }

    @Override
    public Collection<Review> findReviewsByFilmId(Long filmId, int count) {
        return jdbcTemplate.query(
                FIND_REVIEWS_BY_FILM,
                rowMapper,
                filmId,
                count
        );
    }

    @Override
    public Collection<Review> findAllReviews(int count) {
        return jdbcTemplate.query(
                FIND_ALL_REVIEWS,
                rowMapper,
                count
        );
    }

    @Override
    public Optional<Review> findReviewById(Long reviewId) {
        return jdbcTemplate.query(
                        FIND_REVIEW_BY_ID,
                        rowMapper,
                        reviewId
                )
                .stream()
                .findFirst();
    }

    @Override
    public void putLike(Long reviewId, Long userId) {
        jdbcTemplate.update(
                PUT_EVALUATION,
                reviewId,
                userId,
                true
        );
    }

    @Override
    public void putDislike(Long reviewId, Long userId) {
        jdbcTemplate.update(
                PUT_EVALUATION,
                reviewId,
                userId,
                false
        );
    }

    @Override
    public boolean deleteLike(Long reviewId, Long userId) {
        int rows = jdbcTemplate.update(
                DELETE_LIKE,
                reviewId,
                userId
        );

        return rows > 0;
    }

    @Override
    public boolean deleteDislike(Long reviewId, Long userId) {
        int rows = jdbcTemplate.update(
                DELETE_DISLIKE,
                reviewId,
                userId
        );

        return rows > 0;
    }

    @Override
    public boolean deleteEvaluation(Long reviewId, Long userId) {
        int rows = jdbcTemplate.update(
                DELETE_EVALUATION,
                reviewId,
                userId
        );

        return rows > 0;
    }
}