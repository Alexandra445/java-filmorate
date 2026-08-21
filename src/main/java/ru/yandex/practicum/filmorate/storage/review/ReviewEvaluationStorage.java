package ru.yandex.practicum.filmorate.storage.review;

public interface ReviewEvaluationStorage {

    void putLike(Long reviewId, Long userId);

    void putDislike(Long reviewId, Long userId);

    boolean deleteLike(Long reviewID, Long userId);

    boolean deleteDislike(Long reviewId, Long userId);

    boolean deleteEvaluation(Long reviewId, Long userId);
}
