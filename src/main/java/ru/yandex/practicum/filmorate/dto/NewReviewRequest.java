package ru.yandex.practicum.filmorate.dto;


import lombok.Data;

@Data
public class NewReviewRequest {
    private String content;
    private Boolean isPositive;
    private Long userId;
    private Long filmId;
}
