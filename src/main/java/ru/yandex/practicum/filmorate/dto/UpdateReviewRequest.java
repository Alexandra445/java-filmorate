package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class UpdateReviewRequest {
    private Long reviewId;
    private String content;
    private Boolean isPositive;

}
