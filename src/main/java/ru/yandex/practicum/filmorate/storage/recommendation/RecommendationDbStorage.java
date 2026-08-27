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
                    WHERE user_id = ?  -- ID целевого пользователя
                ) my_likes
                JOIN likes other_likes ON other_likes.film_id = my_likes.film_id
                WHERE other_likes.user_id != ?
                GROUP BY other_likes.user_id
                ORDER BY cnt DESC
                LIMIT 1
            )
            SELECT f.*
            FROM films f
            JOIN likes l ON l.film_id = f.id
            JOIN similar_user su ON su.similar_id = l.user_id
            LEFT JOIN likes my_likes ON my_likes.film_id = l.film_id
                                     AND my_likes.user_id = ?  -- ID целевого пользователя
            WHERE my_likes.film_id IS NULL;
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
