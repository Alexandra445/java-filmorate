package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    @Test
    void testCreateUser() {

        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("login");
        user.setName("Alex");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userStorage.create(user);

        assertThat(created.getId()).isNotNull();
    }

    @Test
    void testFindUserById() {

        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("login");
        user.setName("Alex");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userStorage.create(user);

        User found = userStorage.findById(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void testUpdateUser() {

        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("login");
        user.setName("Alex");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userStorage.create(user);

        created.setName("New Name");

        userStorage.update(created);

        User updated = userStorage.findById(created.getId());

        assertThat(updated.getName()).isEqualTo("New Name");
    }

    @Test
    void testFindAllUsers() {

        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("login");
        user.setName("Alex");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        userStorage.create(user);

        assertThat(userStorage.findAll()).isNotEmpty();
    }

    @Test
    void testDeleteUser() {

        User user = new User();
        user.setEmail("delete@mail.ru");
        user.setLogin("delete");
        user.setName("Delete");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userStorage.create(user);

        userStorage.delete(created.getId());

        assertThat(userStorage.findById(created.getId())).isNull();
    }

    @Test
    void testAddFriend() {

        User user1 = new User();
        user1.setEmail("user1@mail.ru");
        user1.setLogin("user1");
        user1.setName("User1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2");
        user2.setName("User2");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        user1 = userStorage.create(user1);
        user2 = userStorage.create(user2);

        userStorage.addFriend(user1.getId(), user2.getId());

        assertThat(userStorage.getFriends(user1.getId()))
                .hasSize(1);
    }

    @Test
    void testRemoveFriend() {

        User user1 = new User();
        user1.setEmail("user1@mail.ru");
        user1.setLogin("user1");
        user1.setName("User1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2");
        user2.setName("User2");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        user1 = userStorage.create(user1);
        user2 = userStorage.create(user2);

        userStorage.addFriend(user1.getId(), user2.getId());
        userStorage.removeFriend(user1.getId(), user2.getId());

        assertThat(userStorage.getFriends(user1.getId()))
                .isEmpty();
    }

    @Test
    void testCommonFriends() {

        User user1 = new User();
        user1.setEmail("user1@mail.ru");
        user1.setLogin("user1");
        user1.setName("User1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2");
        user2.setName("User2");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        User user3 = new User();
        user3.setEmail("user3@mail.ru");
        user3.setLogin("user3");
        user3.setName("User3");
        user3.setBirthday(LocalDate.of(2000, 1, 1));

        user1 = userStorage.create(user1);
        user2 = userStorage.create(user2);
        user3 = userStorage.create(user3);

        userStorage.addFriend(user1.getId(), user3.getId());
        userStorage.addFriend(user2.getId(), user3.getId());

        assertThat(userStorage.getCommonFriends(user1.getId(), user2.getId()))
                .hasSize(1);
    }


}