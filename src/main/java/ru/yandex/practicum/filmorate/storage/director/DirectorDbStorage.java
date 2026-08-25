package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;

@Repository
@Qualifier("directorDbStorage")
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DirectorRowMapper mapper = new DirectorRowMapper();

    @Override
    public Collection<Director> findAll() {
        String sql = """
                SELECT *
                FROM directors
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public Director findById(Integer id) {
        String sql = """
                SELECT *
                FROM directors
                WHERE id = ?
                """;

        return jdbcTemplate.query(sql, mapper, id)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Director add(Director director) {
        String sql = """
                INSERT INTO directors(name)
                VALUES (?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, director.getName());

            return statement;
        }, keyHolder);

        director.setId(keyHolder.getKey().intValue());

        return director;
    }

    @Override
    public Director update(Director director) {
        String sql = """
                UPDATE directors
                SET name = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                director.getName(),
                director.getId()
        );

        return director;
    }

    @Override
    public void delete(Integer id) {

        jdbcTemplate.update(
                "DELETE FROM directors WHERE id = ?",
                id
        );
    }
}