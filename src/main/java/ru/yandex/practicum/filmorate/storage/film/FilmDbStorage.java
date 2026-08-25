package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashSet;

@Repository
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper mapper = new FilmRowMapper();

    @Override
    public Collection<Film> findAll() {

        Collection<Film> films = jdbcTemplate.query("SELECT * FROM films", mapper);

        for (Film film : films) {
            loadMpa(film);
            loadGenres(film);
            loadDirectors(film);
        }

        return films;
    }

    @Override
    public Film findById(Integer id) {

        String sql = "SELECT * FROM films WHERE id=?";

        Film film = jdbcTemplate.query(sql, mapper, id)
                .stream()
                .findFirst()
                .orElse(null);

        if (film != null) {
            loadMpa(film);
            loadGenres(film);
            loadDirectors(film);
        }

        return film;
    }

    @Override
    public Film create(Film film) {

        String sql = """
                INSERT INTO films(name, description, release_date, duration, mpa_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement statement = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());
            statement.setInt(5, film.getMpa().getId());

            return statement;

        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());

        if (film.getGenres() != null) {

            String genreSql = """
                    INSERT INTO film_genres (film_id, genre_id)
                    VALUES (?, ?)
                    """;

            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(
                        genreSql,
                        film.getId(),
                        genre.getId()
                );
            }
        }

        if (film.getDirectors() != null) {

            String directorSql = """
                    INSERT INTO film_directors (film_id, director_id)
                    VALUES (?, ?)
                    """;

            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(
                        directorSql,
                        film.getId(),
                        director.getId()
                );
            }
        }

        return film;
    }

    @Override
    public Film update(Film film) {

        String sql = """
                UPDATE films
                SET name=?,
                    description=?,
                    release_date=?,
                    duration=?,
                    mpa_id=?
                WHERE id=?
                """;

        jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        jdbcTemplate.update(
                "DELETE FROM film_genres WHERE film_id = ?",
                film.getId()
        );

        if (film.getGenres() != null) {

            String genreSql = """
                    INSERT INTO film_genres(film_id, genre_id)
                    VALUES (?, ?)
                    """;

            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(
                        genreSql,
                        film.getId(),
                        genre.getId()
                );
            }
        }

        jdbcTemplate.update(
                "DELETE FROM film_directors WHERE film_id = ?",
                film.getId()
        );

        if (film.getDirectors() != null) {

            String directorSql = """
                    INSERT INTO film_directors(film_id, director_id)
                    VALUES (?, ?)
                    """;

            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(
                        directorSql,
                        film.getId(),
                        director.getId()
                );
            }
        }

        return film;
    }

    @Override
    public void delete(Integer id) {

        jdbcTemplate.update(
                "DELETE FROM film_directors WHERE film_id = ?",
                id
        );

        jdbcTemplate.update(
                "DELETE FROM film_genres WHERE film_id = ?",
                id
        );

        jdbcTemplate.update(
                "DELETE FROM likes WHERE film_id = ?",
                id
        );

        jdbcTemplate.update(
                "DELETE FROM films WHERE id=?",
                id
        );
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM films WHERE id = ?", id);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {

        String sql = """
                INSERT INTO likes (film_id, user_id)
                VALUES (?, ?)
                """;

        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {

        String sql = """
                DELETE FROM likes
                WHERE film_id = ? AND user_id = ?
                """;

        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        StringBuilder sql = new StringBuilder(
                "SELECT f.* FROM films f " +
                        "LEFT JOIN likes l ON f.id = l.film_id "
        );

        if (genreId != null) {
            sql.append("JOIN film_genres fg ON f.id = fg.film_id AND fg.genre_id = ? ");
        }

        if (year != null) {
            sql.append("WHERE EXTRACT(YEAR FROM f.release_date) = ? ");
        }

        sql.append("GROUP BY f.id ORDER BY COUNT(l.user_id) DESC LIMIT ?");

        java.util.List<Object> params = new java.util.ArrayList<>();
        if (genreId != null) {
            params.add(genreId);
        }
        if (year != null) {
            params.add(year);
        }
        params.add(count);

        Collection<Film> films = jdbcTemplate.query(sql.toString(), mapper, params.toArray());

        for (Film film : films) {
            loadMpa(film);
            loadGenres(film);
            loadDirectors(film);
        }

        return films;
    }

    @Override
    public Collection<Film> getFilmsByDirector(Integer directorId, String sortBy) {

        String orderBy;

        if ("year".equals(sortBy)) {
            orderBy = "f.release_date";
        } else {
            orderBy = "COUNT(l.user_id) DESC";
        }

        String sql = """
                SELECT f.*
                FROM films f
                JOIN film_directors fd ON f.id = fd.film_id
                LEFT JOIN likes l ON f.id = l.film_id
                WHERE fd.director_id = ?
                GROUP BY f.id
                ORDER BY %s
                """.formatted(orderBy);

        Collection<Film> films = jdbcTemplate.query(sql, mapper, directorId);

        for (Film film : films) {
            loadMpa(film);
            loadGenres(film);
            loadDirectors(film);
        }

        return films;
    }

    @Override
    public Collection<Film> searchFilms(String query, String by) {

        String searchPattern = "%" + query.toLowerCase() + "%";

        String sql;

        if ("title".equals(by)) {

            sql = """
                    SELECT f.*
                    FROM films f
                    WHERE LOWER(f.name) LIKE ?
                    ORDER BY (
                        SELECT COUNT(*)
                        FROM likes l
                        WHERE l.film_id = f.id
                    ) DESC
                    """;

            Collection<Film> films = jdbcTemplate.query(
                    sql,
                    mapper,
                    searchPattern
            );

            for (Film film : films) {
                loadMpa(film);
                loadGenres(film);
                loadDirectors(film);
            }

            return films;

        } else if ("director".equals(by)) {

            sql = """
                    SELECT f.*
                    FROM films f
                    WHERE EXISTS (
                        SELECT 1
                        FROM film_directors fd
                        JOIN directors d ON d.id = fd.director_id
                        WHERE fd.film_id = f.id
                          AND LOWER(d.name) LIKE ?
                    )
                    ORDER BY (
                        SELECT COUNT(*)
                        FROM likes l
                        WHERE l.film_id = f.id
                    ) DESC
                    """;

            Collection<Film> films = jdbcTemplate.query(
                    sql,
                    mapper,
                    searchPattern
            );

            for (Film film : films) {
                loadMpa(film);
                loadGenres(film);
                loadDirectors(film);
            }

            return films;

        } else {

            sql = """
                    SELECT f.*
                    FROM films f
                    WHERE LOWER(f.name) LIKE ?
                       OR EXISTS (
                           SELECT 1
                           FROM film_directors fd
                           JOIN directors d ON d.id = fd.director_id
                           WHERE fd.film_id = f.id
                             AND LOWER(d.name) LIKE ?
                       )
                    ORDER BY (
                        SELECT COUNT(*)
                        FROM likes l
                        WHERE l.film_id = f.id
                    ) DESC
                    """;

            Collection<Film> films = jdbcTemplate.query(
                    sql,
                    mapper,
                    searchPattern,
                    searchPattern
            );

            for (Film film : films) {
                loadMpa(film);
                loadGenres(film);
                loadDirectors(film);
            }

            return films;
        }
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

    @Override
    public Collection<Film> getCommonFilms(Integer userId, Integer friendId) {
        String sql = "SELECT f.* FROM films f " +
                "JOIN likes l1 ON f.id = l1.film_id AND l1.user_id = ? " +
                "JOIN likes l2 ON f.id = l2.film_id AND l2.user_id = ? " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(l.user_id) DESC";

        Collection<Film> films = jdbcTemplate.query(sql, mapper, userId, friendId);

        for (Film film : films) {
            loadMpa(film);
            loadGenres(film);
        }

        return films;
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        return getPopularFilms(count, null, null);
    }
}