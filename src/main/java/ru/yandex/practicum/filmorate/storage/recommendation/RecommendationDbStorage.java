package ru.yandex.practicum.filmorate.storage.recommendation;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.LinkedHashSet;

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

        Collection<Film> films = jdbcTemplate.query(
                FIND_RECOMMEND_FILMS,
                filmRowMapper,
                userId,
                userId,
                userId
        );

        for (Film film : films) {
            loadMpa(film);
            loadGenres(film);
            loadDirectors(film);
        }

        return films;
    }

    private void loadMpa(Film film) {

        String sql = """
            SELECT id, name
            FROM mpa
            WHERE id = ?
            """;

        film.setMpa(jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {
                    Mpa mpa = new Mpa();
                    mpa.setId(rs.getInt("id"));
                    mpa.setName(rs.getString("name"));
                    return mpa;
                },
                film.getMpa().getId()
        ));
    }

    private void loadGenres(Film film) {

        String sql = """
            SELECT g.id, g.name
            FROM genres g
            JOIN film_genres fg ON g.id = fg.genre_id
            WHERE fg.film_id = ?
            ORDER BY g.id
            """;

        film.setGenres(new LinkedHashSet<>(
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> {
                            Genre genre = new Genre();
                            genre.setId(rs.getInt("id"));
                            genre.setName(rs.getString("name"));
                            return genre;
                        },
                        film.getId()
                )
        ));
    }

    private void loadDirectors(Film film) {

        String sql = """
            SELECT d.id, d.name
            FROM directors d
            JOIN film_directors fd ON d.id = fd.director_id
            WHERE fd.film_id = ?
            ORDER BY d.id
            """;

        film.setDirectors(new LinkedHashSet<>(
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> {
                            Director director = new Director();
                            director.setId(rs.getInt("id"));
                            director.setName(rs.getString("name"));
                            return director;
                        },
                        film.getId()
                )
        ));
    }
}
