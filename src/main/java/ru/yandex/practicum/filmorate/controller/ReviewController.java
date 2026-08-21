package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{id}")
    public ReviewDto getReviewById(@PathVariable Long id) {
        return reviewService.findReviewsById(id);
    }

    @GetMapping
    public Collection<ReviewDto> getReviewsByFilm(@RequestParam(required = false) Long filmId,
                                                  @RequestParam(defaultValue = "10") int count) {
        if (filmId == null) {
            return reviewService.findAllReviews(count);
        } else {
            return reviewService.findReviewsByFilm(filmId, count);
        }
    }

    @PostMapping
    public ReviewDto addReview(@RequestBody NewReviewRequest newReviewRequest) {
        return reviewService.addReview(newReviewRequest);
    }

    @PutMapping
    public ReviewDto updateReview(@RequestBody UpdateReviewRequest updateReviewRequest) {
        return reviewService.updateReview(updateReviewRequest);
    }

    @PutMapping("/{id}/like/{userId}")
    public void putLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.putLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void putDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.putDislike(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.deleteEvaluation(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.deleteEvaluation(id, userId);
    }
}
