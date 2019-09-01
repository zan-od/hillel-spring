package hillel.spring.doctor.service;

import hillel.spring.doctor.domain.DoctorRecord;
import hillel.spring.doctor.domain.Review;
import hillel.spring.doctor.dto.ReviewReportAverageRatingDto;
import hillel.spring.doctor.exception.BadRequestException;
import hillel.spring.doctor.exception.ResourceNotFoundException;
import hillel.spring.doctor.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.util.Assert.notNull;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final DoctorScheduleService doctorScheduleService;
    private final Clock clock;

    public Optional<Review> findById(Integer id) {
        return reviewRepository.findById(id);
    }

    public Review save(Review review) {
        notNull(review.getDoctorRecordId(), "Doctor record id must be not null");

        if (isEmptyReview(review)) {
            throw new BadRequestException("Review must contain at least one rating or comment");
        }
        if (!reviewRatingsValid(review)) {
            throw new BadRequestException("Rating values must be in range 1-5");
        }

        // review date is set to current after every update
        review.setReviewDate(LocalDateTime.now(clock));

        Optional<DoctorRecord> maybeDoctorRecord = doctorScheduleService.findById(review.getDoctorRecordId());
        if (maybeDoctorRecord.isEmpty()) {
            throw new ResourceNotFoundException(String.format("Doctor record with id=%d not found", review.getDoctorRecordId()));
        }

        DoctorRecord doctorRecord = maybeDoctorRecord.get();
        if (review.getReviewDate().compareTo(doctorRecord.getStartDate()) <= 0) {
            throw new BadRequestException(String.format(
                    "Review date %s can not be earlier than doctor record date %s"
                    , review.getReviewDate(), doctorRecord.getStartDate()));
        }

        return reviewRepository.save(review);
    }

    public ReviewReportAverageRatingDto getAverageRatings() {
        ReviewReportAverageRatingDto dto = reviewRepository.getRatingsAverages();
        dto.setReviewComments(reviewRepository.getReviewComments());
        return dto;
    }

    public Page<Review> listReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }

    private boolean isEmptyReview(Review review) {
        return (review.getServiceRating().isEmpty()) && (review.getEquipmentRating().isEmpty()) &&
                (review.getQualificationRating().isEmpty()) && (review.getTreatmentRating().isEmpty()) &&
                (review.getTotalRating().isEmpty()) && (review.getReviewComment().isEmpty());
    }

    private boolean reviewRatingsValid(Review review) {
        return isRatingValid(review.getServiceRating()) && isRatingValid(review.getEquipmentRating()) &&
                isRatingValid(review.getQualificationRating()) && isRatingValid(review.getTreatmentRating()) &&
                isRatingValid(review.getTotalRating());
    }

    @SuppressWarnings(value = "OptionalUsedAsFieldOrParameterType")
    private boolean isRatingValid(Optional<Integer> value) {
        return value.isEmpty() || ((value.get() >= 1) && (value.get() <= 5));
    }
}
