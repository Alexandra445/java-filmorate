package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import java.util.Collection;

public interface DirectorStorage {

    Collection<Director> findAll();

    Director findById(Integer id);

    Director add(Director director);

    Director update(Director director);

    void delete(Integer id);
}
