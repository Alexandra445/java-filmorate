package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Long id;
    private String content;
    private Boolean isPositive;
    private Long userId;
    private Long filmId;
    private long useful;
}
