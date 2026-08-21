package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;

@Repository
@Qualifier("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper mapper = new UserRowMapper();

    @Override
    public Collection<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", mapper);
    }

    @Override
    public User findById(Integer id) {

        String sql = "SELECT * FROM users WHERE id=?";

        return jdbcTemplate.query(sql, mapper, id).stream().findFirst().orElse(null);
    }

    @Override
    public User create(User user) {


        String sql = """
                INSERT INTO users(email, login, name, birthday)
                VALUES (?, ?, ?, ?)
                """;


        KeyHolder keyHolder = new GeneratedKeyHolder();


        jdbcTemplate.update(connection -> {


            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);


            statement.setString(1, user.getEmail());


            statement.setString(2, user.getLogin());


            statement.setString(3, user.getName());


            statement.setDate(4, Date.valueOf(user.getBirthday()));


            return statement;


        }, keyHolder);


        user.setId(keyHolder.getKey().intValue());


        return user;
    }


    @Override
    public User update(User user) {


        String sql = """
                UPDATE users
                SET email=?,
                    login=?,
                    name=?,
                    birthday=?
                WHERE id=?
                """;


        jdbcTemplate.update(sql,

                user.getEmail(), user.getLogin(), user.getName(), Date.valueOf(user.getBirthday()), user.getId());


        return user;
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {

        String sql = """
                INSERT INTO friends (user_id, friend_id, status_id)
                VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(sql, userId, friendId, 1);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {

        String sql = """
                DELETE FROM friends
                WHERE user_id = ? AND friend_id = ?
                """;

        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public Collection<User> getFriends(Integer userId) {

        String sql = """
                SELECT u.*
                FROM users u
                JOIN friends f ON u.id = f.friend_id
                WHERE f.user_id = ?
                """;

        return jdbcTemplate.query(sql, mapper, userId);
    }

    @Override
    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {

        String sql = """
                SELECT u.*
                FROM users u
                JOIN friends f1 ON u.id = f1.friend_id
                JOIN friends f2 ON u.id = f2.friend_id
                WHERE f1.user_id = ?
                  AND f2.user_id = ?
                """;

        return jdbcTemplate.query(sql, mapper, userId, otherId);
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }
}

