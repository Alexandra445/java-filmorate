package ru.yandex.practicum.filmorate.storage.recommendation;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmRowMapper;

import java.util.Collection;

@Repository
public class RecommendationDbStorage implements RecommendationStorage {


    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    private static final String FIND_RECOMMEND_FILMS = """
            WITH similar_user AS (
                            SELECT other_likes.user_id AS similar_id,
                                   COUNT(*) AS cnt
                            FROM (
                                SELECT film_id
                                FROM likes
                                WHERE user_id = ?
                            ) AS my_likes
                            JOIN likes AS other_likes ON other_likes.film_id = my_likes.film_id
                            WHERE other_likes.user_id != ?
                            GROUP BY other_likes.user_id
                            ORDER BY cnt DESC
                            LIMIT 1
                        )
                        SELECT f.*
                        FROM films AS f
                        WHERE f.id IN (
                            SELECT l.film_id
                            FROM likes AS l
                            JOIN similar_user AS su ON su.similar_id = l.user_id
                              AND NOT EXISTS (
                                  SELECT 1
                                  FROM likes my_likes
                                  WHERE my_likes.user_id = ?
                                    AND my_likes.film_id = l.film_id
                              )
                        )
            """;

    public RecommendationDbStorage(JdbcTemplate jdbcTemplate,
                                   FilmRowMapper filmRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
    }

    @Override
    public Collection<Film> getRecommendedFilms(Long userId) {
        return jdbcTemplate.query(FIND_RECOMMEND_FILMS, filmRowMapper, userId, userId, userId);
    }


}
