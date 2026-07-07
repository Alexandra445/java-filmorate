package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService service;


    @BeforeEach
    void setUp() {

        service = new UserService(
                new InMemoryUserStorage()
        );
    }


    @Test
    void shouldCreateValidUser() {

        User user = new User();

        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));


        User result = service.create(user);


        assertNotNull(result.getId());
    }


    @Test
    void shouldFailWhenEmailEmpty() {

        User user = new User();

        user.setEmail("");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));


        assertThrows(
                ValidationException.class,
                () -> service.create(user)
        );
    }


    @Test
    void shouldFailWhenEmailInvalid() {

        User user = new User();

        user.setEmail("wrong-email");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));


        assertThrows(
                ValidationException.class,
                () -> service.create(user)
        );
    }


    @Test
    void shouldFailWhenLoginHasSpace() {

        User user = new User();

        user.setEmail("test@mail.com");
        user.setLogin("bad login");
        user.setBirthday(LocalDate.of(2000, 1, 1));


        assertThrows(
                ValidationException.class,
                () -> service.create(user)
        );
    }


    @Test
    void shouldFailWhenBirthdayInFuture() {

        User user = new User();

        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1));


        assertThrows(
                ValidationException.class,
                () -> service.create(user)
        );
    }
}