package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {

        normalizeUser(user);
        validateUser(user);

        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("Создан пользователь: {}", user);

        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {

        if (user.getId() == null) {
            log.error("Не указан id пользователя");
            throw new ValidationException("Id должен быть указан");
        }

        User existingUser = users.get(user.getId());

        if (existingUser == null) {
            log.error("Пользователь не найден");
            throw new ValidationException("Пользователь не найден");
        }

        normalizeUser(user);
        validateUser(user);

        users.put(user.getId(), user);

        log.info("Обновлён пользователь: {}", user);

        return user;
    }

    private void normalizeUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void validateUser(User user) {

        if (user.getEmail() == null
                || user.getEmail().isBlank()
                || !user.getEmail().contains("@")) {
            log.error("Некорректный email");
            throw new ValidationException("Некорректный email");
        }

        if (user.getLogin() == null
                || user.getLogin().isBlank()
                || user.getLogin().contains(" ")) {
            log.error("Некорректный логин");
            throw new ValidationException("Некорректный логин");
        }

        if (user.getBirthday() == null
                || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения некорректна");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private Integer getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}