package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, UserDbStorage.class})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private UserDbStorage userStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testCreateFilm() {

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Film created = filmStorage.create(film);

        assertThat(created.getId()).isNotNull();
    }

    @Test
    void testFindFilmById() {

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Film created = filmStorage.create(film);

        Film found = filmStorage.findById(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Film");
    }

    @Test
    void testUpdateFilm() {

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Film created = filmStorage.create(film);

        created.setName("Updated Film");

        filmStorage.update(created);

        Film updated = filmStorage.findById(created.getId());

        assertThat(updated.getName()).isEqualTo("Updated Film");
    }

    @Test
    void testFindAllFilms() {

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        filmStorage.create(film);

        assertThat(filmStorage.findAll()).isNotEmpty();
    }

    @Test
    void testDeleteFilm() {

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Film created = filmStorage.create(film);

        filmStorage.delete(created.getId());

        assertThat(filmStorage.findById(created.getId())).isNull();
    }

    @Test
    void testAddLike() {

        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("user");
        user.setName("User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        user = userStorage.create(user);

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        film = filmStorage.create(film);

        filmStorage.addLike(film.getId(), user.getId());

        assertThat(filmStorage.getPopularFilms(10)).hasSize(1);
    }

    @Test
    void testRemoveLike() {

        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("user");
        user.setName("User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        user = userStorage.create(user);

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        film = filmStorage.create(film);

        filmStorage.addLike(film.getId(), user.getId());
        filmStorage.removeLike(film.getId(), user.getId());

        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM likes
                WHERE film_id = ?
                  AND user_id = ?
                """, Integer.class, film.getId(), user.getId());

        assertThat(count).isZero();
    }
}