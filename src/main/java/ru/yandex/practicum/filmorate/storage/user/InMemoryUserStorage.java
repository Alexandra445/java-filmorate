package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Qualifier("inMemoryUserStorage")
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {

        User oldUser = users.get(user.getId());

        oldUser.setEmail(user.getEmail());
        oldUser.setLogin(user.getLogin());
        oldUser.setName(user.getName());
        oldUser.setBirthday(user.getBirthday());

        return oldUser;
    }

    @Override
    public User findById(Integer id) {
        return users.get(id);
    }

    private Integer getNextId() {
        int currentMaxId = users.keySet().stream().mapToInt(id -> id).max().orElse(0);

        return ++currentMaxId;
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {

        User user = users.get(userId);

        if (user != null) {
            user.getFriends().put(friendId, null);
        }
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {

        User user = users.get(userId);

        if (user != null) {
            user.getFriends().remove(friendId);
        }
    }

    @Override
    public Collection<User> getFriends(Integer userId) {

        User user = users.get(userId);

        if (user == null) {
            return java.util.Collections.emptyList();
        }

        return user.getFriends().keySet().stream().map(users::get).toList();
    }

    @Override
    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {

        User user = users.get(userId);
        User otherUser = users.get(otherId);

        if (user == null || otherUser == null) {
            return java.util.Collections.emptyList();
        }

        return user.getFriends().keySet().stream().filter(otherUser.getFriends().keySet()::contains).map(users::get).toList();
    }

    @Override
    public void delete(Integer id) {
        users.remove(id);
    }
}