package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Repository
@Qualifier("mpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaRowMapper mapper = new MpaRowMapper();

    @Override
    public Collection<Mpa> findAll() {

        String sql = """
                SELECT *
                FROM mpa
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public Mpa findById(Integer id) {

        String sql = """
                SELECT *
                FROM mpa
                WHERE id = ?
                """;

        return jdbcTemplate.query(sql, mapper, id).stream().findFirst().orElse(null);
    }
}