package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.DataCanNotBeUpdatedException;
import ru.yandex.practicum.filmorate.exception.ReturnGeneratedIdException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseStorage<T> {

    protected final JdbcTemplate jdbcTemplate;
    protected final RowMapper<T> rowMapper;

    protected Long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        if (id != null) {
            return id;
        } else {
            throw new ReturnGeneratedIdException("Не удалось вернуть/сгенерированный id");
        }
    }

    protected void insertWithoutId(String query, Object... params) {
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(query, Statement.NO_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            return ps;
        });
    }

    protected boolean delete(String query, Object... params) {
        int rowDeleted = jdbcTemplate.update(query, params);
        return  rowDeleted > 0;
    }

    protected int update(String query, Object... params) {
        int rowUpdated = jdbcTemplate.update(query, params);
        if (rowUpdated == 0) {
            throw new DataCanNotBeUpdatedException("Данные не были обновлены");
        }
        return rowUpdated;
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbcTemplate.query(query, rowMapper, params);
    }

    protected Optional<T> findAny(String query, Object... params) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(query, rowMapper, params));
    }
}
