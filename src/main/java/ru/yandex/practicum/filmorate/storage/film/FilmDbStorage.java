package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
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
        
        Collection<Film> films = jdbcTemplate.query(
                "SELECT * FROM films",
                mapper
        );

        for (Film film : films) {
            loadMpa(film);
            loadGenres(film);
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

            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

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

                jdbcTemplate.update(genreSql, film.getId(), genre.getId());
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

        jdbcTemplate.update(sql, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId(), film.getId());

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        if (film.getGenres() != null) {

            String genreSql = """
                    INSERT INTO film_genres(film_id, genre_id)
                    VALUES (?, ?)
                    """;

            for (Genre genre : film.getGenres()) {

                jdbcTemplate.update(genreSql, film.getId(), genre.getId());
            }
        }
        return film;
    }

    @Override
    public void delete(Integer id) {

        jdbcTemplate.update("DELETE FROM films WHERE id=?", id);
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
    public Collection<Film> getPopularFilms(Integer count) {

        String sql = """
                SELECT f.*
                FROM films f
                LEFT JOIN likes l ON f.id = l.film_id
                GROUP BY f.id
                ORDER BY COUNT(l.user_id) DESC
                LIMIT ?
                """;

        return jdbcTemplate.query(sql, mapper, count);
    }

    private void loadMpa(Film film) {
        String sql = """
            SELECT id, name
            FROM mpa
            WHERE id = ?
            """;

        film.setMpa(
                jdbcTemplate.queryForObject(
                        sql,
                        (rs, rowNum) -> {
                            Mpa mpa = new Mpa();
                            mpa.setId(rs.getInt("id"));
                            mpa.setName(rs.getString("name"));
                            return mpa;
                        },
                        film.getMpa().getId()
                )
        );
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
}