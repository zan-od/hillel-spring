package hillel.spring.doctor.service;

import hillel.spring.doctor.domain.DoctorRecord;
import hillel.spring.doctor.domain.Review;
import hillel.spring.doctor.dto.ReviewReportAverageRatingDto;
import hillel.spring.doctor.exception.BadRequestException;
import hillel.spring.doctor.exception.ResourceNotFoundException;
import hillel.spring.doctor.repository.ReviewRepository;
import lombok.AllArgsConstructor;
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

    public Review create(Review review) {
        validateReview(review);

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

    private void validateReview(Review review) {
        notNull(review.getDoctorRecordId(), "Doctor record id must be not null");

        if (review.isEmpty()) {
            throw new BadRequestException("Review must contain at least one rating or comment");
        }
        if (!review.ratingsValid()) {
            throw new BadRequestException("Rating values must be in range 1-5");
        }
    }

    public Review save(Review review) {
        validateReview(review);

        return reviewRepository.save(review);
    }

    public ReviewReportAverageRatingDto getAverageRatings() {
        ReviewReportAverageRatingDto dto = reviewRepository.getRatingsAverages();
        dto.setReviewComments(reviewRepository.getReviewComments());
        return dto;
    }

}
