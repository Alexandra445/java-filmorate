package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Review {
    private Long id;
    private String content;
    private Boolean isPositive;
    private Long userId;
    private Long filmId;
    private long useful;
}
