package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.model.Review;

public class ReviewMapper {

    public static Review mapToReview(NewReviewRequest newReviewRequest) {
        Review review = new Review();
        review.setContent(newReviewRequest.getContent());
        review.setIsPositive(newReviewRequest.getIsPositive());
        review.setUserId(newReviewRequest.getUserId());
        review.setFilmId(newReviewRequest.getFilmId());

        return review;
    }

    public static ReviewDto mapToReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setReviewId(review.getId());
        reviewDto.setContent(review.getContent());
        reviewDto.setUserId(review.getUserId());
        reviewDto.setFilmId(review.getFilmId());
        reviewDto.setIsPositive(review.getIsPositive());
        reviewDto.setUseful(reviewDto.getUseful());
        return reviewDto;
    }

    public static Review updateFields(Review reviewToUpdate, UpdateReviewRequest updateReviewRequest) {
        if (updateReviewRequest.getIsPositive() != null) {
            reviewToUpdate.setIsPositive(updateReviewRequest.getIsPositive());
        }
        if (updateReviewRequest.getContent() != null && !updateReviewRequest.getContent().isBlank()) {
            reviewToUpdate.setContent(updateReviewRequest.getContent());
        }
        return reviewToUpdate;
    }
}
