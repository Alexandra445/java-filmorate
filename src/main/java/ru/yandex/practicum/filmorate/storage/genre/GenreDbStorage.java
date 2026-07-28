package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Repository
@Qualifier("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper mapper = new GenreRowMapper();

    @Override
    public Collection<Genre> findAll() {

        String sql = """
                SELECT *
                FROM genres
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public Genre findById(Integer id) {

        String sql = """
                SELECT *
                FROM genres
                WHERE id = ?
                """;

        return jdbcTemplate.query(sql, mapper, id).stream().findFirst().orElse(null);
    }
}