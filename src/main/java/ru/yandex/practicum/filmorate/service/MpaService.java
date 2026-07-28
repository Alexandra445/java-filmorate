package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {

    @Qualifier("mpaDbStorage")
    private final MpaStorage mpaStorage;

    public Collection<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    public Mpa findById(Integer id) {

        Mpa mpa = mpaStorage.findById(id);

        if (mpa == null) {
            throw new NotFoundException("Рейтинг не найден");
        }

        return mpa;
    }

}