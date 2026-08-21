package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewEvaluationStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewMapper;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewEvaluationStorage reviewEvaluationStorage;


    public ReviewDto addReview(NewReviewRequest newReviewRequest) {
        validateFields(newReviewRequest);
        Review review = ReviewMapper.mapToReview(newReviewRequest);
        review = reviewStorage.addReview(review);
        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto updateReview(UpdateReviewRequest updateReviewRequest) {
        Review updatedReview = reviewStorage
                .findReviewById(updateReviewRequest.getReviewId())
                .orElseThrow(() -> {
                    log.debug("Отзыва с id = {} не существует", updateReviewRequest.getReviewId());
                    return new ReviewNotFoundException("Отзыв для обновления не найден");
                });
        ReviewMapper.updateFields(updatedReview, updateReviewRequest);
        return ReviewMapper.mapToReviewDto(updatedReview);
    }

    public void deleteReview(Long reviewId) {
        if (!reviewStorage.deleteReview(reviewId)) {
            throw new ReviewNotFoundException("Отзыв для удаления не был найден");
        }
    }

    public ReviewDto findReviewsById(Long reviewId) {
        return ReviewMapper.mapToReviewDto(reviewStorage
                .findReviewById(reviewId)
                .orElseThrow(() -> {
                    log.debug("Отзыва с указанным id не существует");
                    return new ReviewNotFoundException("Отзыва не существует");
                }));
    }

    public Collection<ReviewDto> findReviewsByFilm(Long filmId, int count) {
        return reviewStorage
                .findReviewsByFilmId(filmId, count)
                .stream()
                .map(ReviewMapper::mapToReviewDto)
                .toList();
    }

    public Collection<ReviewDto> findAllReviews(int count) {
        return reviewStorage.findAllReviews(count).stream()
                .map(ReviewMapper::mapToReviewDto)
                .toList();
    }

    public void putLike(Long reviewId, Long userId) {
        reviewEvaluationStorage.deleteEvaluation(reviewId, userId);
        reviewEvaluationStorage.putLike(reviewId, userId);
    }

    public void putDislike(Long reviewId, Long userId) {
        reviewEvaluationStorage.deleteEvaluation(reviewId, userId);
        reviewEvaluationStorage.putDislike(reviewId, userId);
    }

    public void deleteEvaluation(Long reviewId, Long userId) {
        reviewEvaluationStorage.deleteEvaluation(reviewId, userId);
    }

    private void validateFields(NewReviewRequest reviewRequest) {
        if (reviewRequest.getContent() == null || reviewRequest.getContent().isBlank()) {
            throw new ValidationException("Содержимое отзыва не может быть пустым");
        }
    }
}
