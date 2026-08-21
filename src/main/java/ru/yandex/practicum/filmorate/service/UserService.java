package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    private final EventStorage eventStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, EventStorage eventStorage) {
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {

        normalizeUser(user);
        validateUser(user);

        return userStorage.create(user);
    }

    public User update(User user) {

        if (user.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        if (userStorage.findById(user.getId()) == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        normalizeUser(user);
        validateUser(user);

        return userStorage.update(user);
    }

    private void normalizeUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void validateUser(User user) {

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Некорректный логин");
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    public User getUser(Integer id) {
        User user = userStorage.findById(id);

        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        return user;
    }

    public void addFriend(Integer id, Integer friendId) {

        getUser(id);
        getUser(friendId);

        userStorage.addFriend(id, friendId);

        log.info("Пользователь {} добавил пользователя {} в друзья", id, friendId);
        eventStorage.addEvent(new Event(null, System.currentTimeMillis(), id, EventType.FRIEND, Operation.ADD, friendId));
    }

    public void removeFriend(Integer id, Integer friendId) {

        getUser(id);
        getUser(friendId);

        userStorage.removeFriend(id, friendId);

        log.info("Пользователь {} удалил пользователя {} из друзей", id, friendId);
        eventStorage.addEvent(new Event(null, System.currentTimeMillis(), id, EventType.FRIEND, Operation.REMOVE, friendId));
    }

    public Collection<User> getFriends(Integer id) {

        getUser(id);

        return userStorage.getFriends(id);
    }

    public Collection<User> getCommonFriends(Integer id, Integer otherId) {

        getUser(id);
        getUser(otherId);

        return userStorage.getCommonFriends(id, otherId);
    }


    public void deleteUser(Integer id) {
        getUser(id);
        userStorage.delete(id);
    }

    public Collection<Event> getFeed(Integer id) {
        getUser(id);
        return eventStorage.getFeedByUserId(id);
    }
}
