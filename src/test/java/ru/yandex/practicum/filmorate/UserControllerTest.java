package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController();
    }

    @Test
    void shouldCreateValidUser() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User result = controller.create(user);

        assertNotNull(result.getId());
    }

    @Test
    void shouldFailWhenEmailEmpty() {
        User user = new User();
        user.setEmail("");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class,
                () -> controller.create(user));
    }

    @Test
    void shouldFailWhenEmailInvalid() {
        User user = new User();
        user.setEmail("wrong-email");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class,
                () -> controller.create(user));
    }

    @Test
    void shouldFailWhenLoginHasSpace() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("bad login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class,
                () -> controller.create(user));
    }

    @Test
    void shouldFailWhenBirthdayInFuture() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class,
                () -> controller.create(user));
    }
}